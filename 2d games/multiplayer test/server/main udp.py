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
all_clients = {}

"""
Reserved characters:
"""

def send_msg(msg, client_addr, server_socket):
    message = msg.encode('utf-8')
    msg_len = len(message)
    send_len = str(msg_len).encode('utf-8')
    send_len += b' ' * (HEADER - len(send_len))
    print(f"Sending message: '{msg}' to client: {client_addr}")
    
    server_socket.sendto(send_len, client_addr)
    server_socket.sendto(message, client_addr)

def execute_command(data, addr, server_socket):
    if "newPlayer=" in data:
        player_name = data.split("=")[1]
        for client_addr in all_clients:
            if client_addr != addr:
                send_msg(f"!newPlayer={player_name}", client_addr, server_socket)
    elif "posUpdate=" in data:
        for client_addr in all_clients:
            if client_addr != addr:
                send_msg(f"!posUpdate={data.split('posUpdate=')[1]}", client_addr, server_socket)
    elif "currentPos=" in data:
        for client_addr in all_clients:
            if client_addr != addr:
                send_msg(f"!oldPlayer={data.split('currentPos=')[1]}", client_addr, server_socket)

def request_all_stats(client_addr, server_socket):
    send_msg("!sendAllPlayerStats", client_addr, server_socket)

def handle_client_connection(server_socket):
    print(f"Server is ready to receive clients via UDP on {ADDR}")
    
    while True:
        try:
            data, addr = server_socket.recvfrom(HEADER)
            data_len = int(data.decode('utf-8').strip())

            message, addr = server_socket.recvfrom(data_len)
            message = message.decode('utf-8')
            print(f"[{addr}] {message}")

            if addr not in all_clients:
                all_clients[addr] = time.time()

            if message == "!BEAT":
                all_clients[addr] = time.time()
            elif message == "!DISCONNECT":
                print(f"{addr} disconnected.")
                del all_clients[addr]
            elif "=" in message:
                execute_command(message, addr, server_socket)
            else:
                send_msg("error|005", addr, server_socket)
            
        except Exception as e:
            print(f"Error occurred with client {addr}. Error: {e}\n{traceback.print_exc()}")

def heartbeat_check(server_socket):
    while True:
        time.sleep(1)
        current_time = time.time()

        for addr in list(all_clients):
            if current_time - all_clients[addr] > 7:
                print(f"{addr} has not sent heartbeat within 7 seconds. Disconnecting...")
                send_msg("BYEBYE", addr, server_socket)
                del all_clients[addr]
            elif current_time - all_clients[addr] > 5:
                send_msg("!heartbeat", addr, server_socket)

def start_server():
    try:
        server_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        server_socket.bind(ADDR)

        print(f"[LISTENING] Server is listening on {SERVER}:{PORT}")

        # Start client handling thread
        client_thread = threading.Thread(target=handle_client_connection, args=(server_socket,))
        client_thread.start()

        # Start heartbeat check thread
        heartbeat_thread = threading.Thread(target=heartbeat_check, args=(server_socket,))
        heartbeat_thread.start()

    except Exception as e:
        print(f"Error starting server: {e}")
        sys.exit(1)

if __name__ == "__main__":
    print(f"Socket is starting...\nADDR:{ADDR}")
    start_server()
