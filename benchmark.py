from scapy.all import sniff
import time
import sys

try:
    print("Starting capture...")
    sys.stdout.flush()
    start = time.time()
    packets = sniff(count=1000, iface="lo", timeout=30)
    end = time.time()
    print(f"Captured {len(packets)} packets")
    elapsed = end - start
    print(f"Time: {elapsed:.4f} seconds")
except Exception as e:
    print(f"ERROR: {e}")
