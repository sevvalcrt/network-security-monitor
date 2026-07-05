from scapy.all import sniff, IP, TCP
import sqlite3
from datetime import datetime

connection_tracker = {}
PORT_SCAN_THRESHOLD = 10
alerted_ips = set()

def load_threat_ips():
    try:
        with open("threat_ips.txt", "r") as f:
            return set(line.strip() for line in f if line.strip())
    except FileNotFoundError:
        print("Warning: threat_ips.txt not found, skipping threat intel checks.")
        return set()

THREAT_IPS = load_threat_ips()

def init_database():
    conn = sqlite3.connect("alerts.db")
    cursor = conn.cursor()
    cursor.execute(""" CREATE TABLE IF NOT EXISTS alerts(id INTEGER PRIMARY KEY AUTOINCREMENT, timestamp TEXT, source_ip TEXT, alert_type TEXT, details TEXT)""")
    conn.commit()
    conn.close()

def save_alert(source_ip, alert_type, details):
    conn = sqlite3.connect("alerts.db")
    cursor = conn.cursor()
    timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    cursor.execute("INSERT INTO alerts (timestamp, source_ip, alert_type, details) VALUES (?, ?, ?, ?)", (timestamp, source_ip, alert_type, details))
    conn.commit()
    conn.close()

def process_packet(packet):
    if packet.haslayer(IP):

        src_ip = packet[IP].src
        dst_ip = packet[IP].dst

        if src_ip in THREAT_IPS and src_ip not in alerted_ips:
            print(f"WARNING: Traffic from known malicious IP detected! Source: {src_ip}")
            save_alert(src_ip, "MALICIOUS_IP", "Matched threat intelligence feed")
            alerted_ips.add(src_ip)

        print(f"Source: {src_ip} -> Destination: {dst_ip}")

        if packet.haslayer(TCP):
            dst_port = packet[TCP].dport
            print(f"    TCP Port: {packet[TCP].sport}  -> {packet[TCP].dport}")
            
            if src_ip not in connection_tracker:
                connection_tracker[src_ip] = set()

            connection_tracker[src_ip].add(dst_port)

            if len(connection_tracker[src_ip]) > PORT_SCAN_THRESHOLD and src_ip not in alerted_ips:
                print(f"WARNING: Possible port scan detected! Source: {src_ip}, Unique ports: {len(connection_tracker[src_ip])}")
                save_alert(src_ip, "PORT_SCAN", f"Unique ports: {len(connection_tracker[src_ip])}")
                alerted_ips.add(src_ip)

init_database()
print("Packet capture started... (press Ctrl+C to stop)")
sniff(prn = process_packet, count = 0, iface = "lo")
