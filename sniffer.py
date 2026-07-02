from scapy.all import sniff, IP, TCP

connection_tracker = {}
PORT_SCAN_THRESHOLD = 10
alerted_ips = set()

def process_packet(packet):
    if packet.haslayer(IP):

        src_ip = packet[IP].src
        dst_ip = packet[IP].dst

        print(f"Source: {src_ip} -> Destination: {dst_ip}")

        if packet.haslayer(TCP):
            dst_port = packet[TCP].dport
            print(f"    TCP Port: {packet[TCP].sport}  -> {packet[TCP].dport}")
            
            if src_ip not in connection_tracker:
                connection_tracker[src_ip] = set()

            connection_tracker[src_ip].add(dst_port)

            if len(connection_tracker[src_ip]) > PORT_SCAN_THRESHOLD and src_ip not in alerted_ips:
                print(f"WARNING: Possible port scan detected! Source: {src_ip}, Unique ports: {len(connection_tracker[src_ip])}")
                alerted_ips.add(src_ip)

print("Packet capture started... (press Ctrl+C to stop)")
sniff(prn = process_packet, count = 0, iface = "lo")
