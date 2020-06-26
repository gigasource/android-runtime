#ifndef TEST_BRIDGE_H_
#define TEST_BRIDGE_H_

typedef void (*t_bridge_callback)(const char* channelName, const char* message);
void SendMessageToNodeChannel(const char* channelName, const char* message);
void RegisterBridgeCallback(t_bridge_callback callback);

#endif