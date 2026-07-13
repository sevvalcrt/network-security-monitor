# Network Security Monitor

A real-time network traffic monitoring tool that captures packets and detects suspicious activity, starting with port scan detection. Built as a learning project to explore network security concepts and threat detection techniques.

## Features
- Real-time packet capture using Scapy
- Port scan detection based on connection tracking (flags source IPs making an unusually high number of connections to different ports)
- Console-based alerting
- Persistent alert logging to a SQLite database
- Java/Spring Boot REST API exposing detected alerts (`/alerts`, `/alerts/port-scan`)
- Independent Java-based detection engine with thread-safe event processing (`/events`)
- C-based packet capture layer using libpcap, with its own port scan detection logic
- Threat intelligence integration: flags traffic from known malicious IPs using a public feed (ipsum project)
- Live web dashboard with real-time stats and alert visualization
- Start/stop packet capture directly from the dashboard (no terminal required)


## How It Works
The tool captures TCP/IP packets on a network interface and tracks, per source IP, the set of unique destination ports it connects to within the capture session. If the number of unique ports exceeds a configurable threshold, it raises a warning — a common signature of port scanning tools like Nmap.
A parallel Java/Spring Boot service exposes a `POST /events` endpoint that accepts network events (source IP and destination port) and runs the same port-scan detection logic independently, using a thread-safe `ConcurrentHashMap` to track connections per source IP across concurrent requests. This demonstrates the same detection algorithm implemented across two different stacks (Python for packet capture, Java for a concurrent, API-driven detection service).

## Tech Stack
- Python 3 (packet capture and detection logic)
- Scapy (packet capture and parsing)
- SQLite (persistent storage)
- Java 21 + Spring Boot (REST API layer)
- Spring Data JPA + Hibernate (database access)
- C + libpcap (low-level packet capture layer)

## Installation & Usage

```bash
git clone https://github.com/sevvalcrt/network-security-monitor.git
cd network-security-monitor
python3 -m venv venv
source venv/bin/activate
pip install scapy
sudo venv/bin/python sniffer.py
```

**Note:** Packet capture requires root/administrator privileges.

### Running the REST API (Java/Spring Boot)

```bash
cd detection-engine
./mvnw spring-boot:run
```

Once running, query the detected alerts:
```bash
curl localhost:8080/alerts
curl localhost:8080/alerts/port-scan
```

Simulate a port scan directly against the Java service:
```bash
for port in 1 2 3 4 5 6 7 8 9 10 11; do
  curl -X POST localhost:8080/events -H "Content-Type: application/json" -d "{\"sourceIp\": \"10.0.0.99\", \"destPort\": $port}"
done
```

Update the threat intelligence feed (downloads known malicious IPs):
```bash
python3 intel_update.py
```
Open the live dashboard in your browser:
```
http://localhost:8080/dashboard.html
```

### Running the C packet capture layer

```bash
cd capture-engine
gcc sniffer.c -o sniffer -lpcap
sudo ./sniffer
```

This captures traffic on the loopback interface and independently detects port scans using its own lightweight tracking structure.

## Testing

Tested locally by running an Nmap scan against localhost and confirming the tool correctly flagged the scanning behavior:

```bash
nmap -p 1-100 localhost
```

### Performance Comparison

A basic benchmark capturing 1000 packets on the loopback interface:

| Implementation | Time |
|---|---|
| C (libpcap) | 0.0068 seconds |
| Python (Scapy) | 3.8873 seconds |

This highlights the performance difference between a compiled, low-level language and a high-level scripting language for raw packet processing — a key motivation for implementing the capture layer in C.

## Roadmap
- [x] Reduce alert noise (rate-limit repeated warnings per source)
- [x] Log detected events to a database (SQLite/PostgreSQL)
- [x] Java-based detection engine with REST API
- [x] C/C++ high-performance packet capture layer
- [x] Threat intelligence feed integration (known malicious IPs)
- [x] Web dashboard for live visualization

## Ethical Use Notice
This tool is intended for educational purposes and authorized security testing only. Only use it on networks and systems you own or have explicit permission to monitor/test.