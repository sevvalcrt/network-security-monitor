#include <stdio.h>
#include <pcap.h>
#include <netinet/ip.h>
#include <netinet/tcp.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <string.h>
#include <time.h>

#define MAX_IPS 50
#define MAX_PORTS_PER_IP 50
#define PORT_SCAN_THRESHOLD 10

struct IpTracker{
    char ip[INET_ADDRSTRLEN];
    int ports[MAX_PORTS_PER_IP];
    int port_count;
    int alerted;
};

struct IpTracker tracker[MAX_IPS];
int tracker_count = 0;

void check_port_scan(const char *ip, int port){
    int ip_index = -1;

    for (int i = 0; i < tracker_count; i++)
    {
        if (strcmp(tracker[i].ip, ip) == 0)
        {
            ip_index = i;
            break;
        }
    }

    if (ip_index == -1)
    {
        if(tracker_count >= MAX_IPS) return;
        ip_index = tracker_count;
        strcpy(tracker[ip_index].ip, ip);
        tracker[ip_index].port_count = 0;
        tracker[ip_index].alerted = 0;
        tracker_count++;
    }

    int port_exists = 0;
    for (int i = 0; i < tracker[ip_index].port_count; i++)
    {
        if (tracker[ip_index].ports[i] == port)
        {
            port_exists = 1;
            break;
        }
    }

     if (!port_exists && tracker[ip_index].port_count < MAX_PORTS_PER_IP) {
        tracker[ip_index].ports[tracker[ip_index].port_count] = port;
        tracker[ip_index].port_count++;
    }

    if (tracker[ip_index].port_count > PORT_SCAN_THRESHOLD && !tracker[ip_index].alerted) {
        printf("WARNING: Possible port scan detected! Source: %s, Unique ports: %d\n", ip, tracker[ip_index].port_count);
        tracker[ip_index].alerted = 1;
    }
}

void packet_handler(u_char *args, const struct pcap_pkthdr *header, const u_char *packet) {
    struct ip *ip_header = (struct ip *)(packet + 14);

    char src_ip[INET_ADDRSTRLEN];
    char dst_ip[INET_ADDRSTRLEN];
    inet_ntop(AF_INET, &(ip_header->ip_src), src_ip, INET_ADDRSTRLEN);
    inet_ntop(AF_INET, &(ip_header->ip_dst), dst_ip, INET_ADDRSTRLEN);

    printf("Source: %s -> Destination: %s\n", src_ip, dst_ip);

    if (ip_header -> ip_p == IPPROTO_TCP)
    {
        int ip_header_len = ip_header -> ip_hl * 4;
        struct tcphdr *tcp_header = (struct tcphdr *)(packet + 14 + ip_header_len);

        int src_port = ntohs(tcp_header -> th_sport);
        int dst_port = ntohs(tcp_header -> th_dport);

        printf("  TCP Port: %d -> %d\n", src_port, dst_port);
        check_port_scan(src_ip, dst_port);
    }
}

int main(){
    char errbuf[PCAP_ERRBUF_SIZE];
    pcap_t *handle;

    handle = pcap_open_live("lo", BUFSIZ, 1, 1000, errbuf);
    if (handle == NULL)
    {
        printf("Could not open device: %s\n", errbuf);
        return 1;
    }

    printf("Packet capture started... (press Ctrl+C to stop)\n");

    clock_t start = clock();
    pcap_loop(handle, 1000, packet_handler, NULL);
    clock_t end = clock();

    double elapsed = (double)(end - start) / CLOCKS_PER_SEC;
    printf("Captured 1000 packets in %.4f seconds\n", elapsed);
    
    pcap_close(handle);
    return 0;
}