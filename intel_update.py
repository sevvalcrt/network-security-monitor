import requests

FEED_URL = "https://raw.githubusercontent.com/stamparm/ipsum/master/levels/3.txt"

def update_threat_list():
    print("Fetching threat intelligence feed...")
    response = requests.get(FEED_URL)
    ips = response.text.strip().split("\n")

    with open("threat_ips.txt", "w") as f:
        f.write("\n".join(ips))

    print(f"Saved {len(ips)} known malicious IPs to threat_ips.txt")

if __name__ == "__main__":
    update_threat_list()
