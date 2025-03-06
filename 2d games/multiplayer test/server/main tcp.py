import os
import select
import socket
import sys
import threading
import time
import traceback

HEADER = 10
PORT = 9991
SERVER = "0.0.0.0"
ADDR = (SERVER, PORT)
HEARTBEAT_INTERVAL = 5
HEARTBEAT_TIMEOUT = 7
all_clients = {}

def send_msg(msg, client):
    """Sends a message to the specified client."""
    message = msg.encode('utf-8')
    msg_len = len(message)
    send_len = str(msg_len).encode('utf-8') + b' ' * (HEADER - len(str(msg_len)))
    print(f"Sending message: '{msg}' to client: {client}")
    client.send(send_len)
    client.send(message)

def execute_command(data, connOrigin, addr):
    """Executes commands based on the received data."""
    if "newPlayer=" in data:
        player_id = data.split("=")[1]
        for conn in all_clients:
            all_clients[conn] = player_id
            send_msg(f"!newPlayer={player_id}", conn)
        print(f"Updated player list: {all_clients}")

    elif "posUpdate=" in data:
        position = data.split("posUpdate=")[1]
        for conn in all_clients:
            if conn != connOrigin:
                send_msg(f"!posUpdate={position}", conn)

    elif "currentPos=" in data:
        current_pos = data.split("currentPos=")[1]
        for conn in all_clients:
            if conn != connOrigin:
                send_msg(f"!oldPlayer={current_pos}", conn)

    elif "disconnect" == data:
        notify_disconnection(connOrigin, addr)
        return "disconnect"

    elif "!disconnect" in data:
        player_id = data.split("=")[1]
        notify_disconnection(connOrigin, addr, player_id)
        return "disconnect"

def notify_disconnection(connOrigin, addr, player_id=None):
    """Handles client disconnection notifications."""
    if not player_id:
        player_id = all_clients[connOrigin]
    
    for conn in all_clients:
        if conn != connOrigin:
            send_msg(f"!disconnect={player_id}", conn)
    
    del all_clients[connOrigin]
    connOrigin.close()
    print(f"{addr} disconnected from the socket.")

def request_all_stats(conn):
    """Requests stats from all clients."""
    send_msg("!sendAllPlayerStats", conn)

def handle_client_connection(conn, addr):
    """Handles incoming client connections."""
    print(f"{addr} connected to the socket.")
    connected = True
    heartbeat_received_time = time.time() - HEARTBEAT_INTERVAL
    heartbeat_send_time = time.time() - HEARTBEAT_INTERVAL
    
    while connected:
        try:
            ready_to_read, _, _ = select.select([conn], [], [], 1)
            
            if ready_to_read:
                data_len = conn.recv(HEADER).decode('utf-8').strip()
                if data_len:
                    data = receive_data(conn, int(data_len))
                    print(f"[{addr}] {data}")
                    heartbeat_received_time = time.time()

                    if data == "!DISCONNECT":
                        connected = False
                    elif data == "!BEAT":
                        heartbeat_received_time = time.time()
                    elif "=" in data:
                        if execute_command(data, conn, addr) == "disconnect":
                            connected = False
                    else:
                        send_msg("error|005", conn)

            current_time = time.time()
            if current_time - heartbeat_send_time > HEARTBEAT_INTERVAL:
                send_msg("!heartbeat", conn)
                heartbeat_send_time = current_time

            if current_time - heartbeat_received_time > HEARTBEAT_TIMEOUT:
                print(f"{addr} has not sent heartbeat within {HEARTBEAT_TIMEOUT} seconds. Disconnecting...")
                send_msg("BYEBYE", conn)
                connected = False
        except (BrokenPipeError, ConnectionResetError):
            print(f"{addr} encountered an error. Disconnecting...")
            connected = False
        except Exception as e:
            print(f"Error occurred with client {addr}. Error: {e}\n{traceback.format_exc()}")
    
    if connected:
        notify_disconnection(conn, addr)

def receive_data(conn, data_len):
    """Receives a specified amount of data from the client."""
    data = bytearray()
    while len(data) < data_len:
        packet = conn.recv(data_len - len(data))
        if not packet:
            break
        data.extend(packet)
    return data.decode('utf-8')

def start_server():
    """Starts the server and listens for incoming connections."""
    server.listen()
    print(f"[LISTENING] Server is listening on {SERVER}:{PORT}")
    while True:
        conn, addr = server.accept()
        all_clients[conn] = None
        thread = threading.Thread(target=handle_client_connection, args=(conn, addr))
        thread.start()
        print(f"Active connections: {threading.active_count() - 1}")

if __name__ == "__main__":
    print(f"Socket is starting...\nADDR:{ADDR}")
    try:
        server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        server.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        server.bind(ADDR)
    except Exception as e:
        print(f"Error starting server: {e}")
        sys.exit(1)
    print("Socket established successfully")

    start_server()
