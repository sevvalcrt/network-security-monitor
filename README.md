# Network Security Monitor

A real-time network traffic monitoring tool that captures packets and detects suspicious activity, starting with port scan detection. Built as a learning project to explore network security concepts and threat detection techniques.

## Features
- Real-time packet capture using Scapy
- Port scan detection based on connection tracking (flags source IPs making an unusually high number of connections to different ports)
- Console-based alerting

## How It Works
The tool captures TCP/IP packets on a network interface and tracks, per source IP, the set of unique destination ports it connects to within the capture session. If the number of unique ports exceeds a configurable threshold, it raises a warning — a common signature of port scanning tools like Nmap.

## Tech Stack
- Python 3
- Scapy (packet capture and parsing)

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

## Testing

Tested locally by running an Nmap scan against localhost and confirming the tool correctly flagged the scanning behavior:

```bash
nmap -p 1-100 localhost
```

## Roadmap
- [ ] Reduce alert noise (rate-limit repeated warnings per source)
- [ ] Log detected events to a database (SQLite/PostgreSQL)
- [ ] Java-based detection engine with REST API
- [ ] C/C++ high-performance packet capture layer
- [ ] Threat intelligence feed integration (known malicious IPs)
- [ ] Web dashboard for live visualization

## Ethical Use Notice
This tool is intended for educational purposes and authorized security testing only. Only use it on networks and systems you own or have explicit permission to monitor/test.