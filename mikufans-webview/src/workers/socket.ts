import { io, Socket } from "socket.io-client";
import { CustomSendMessage, CustomReceiveMessage } from "@/hooks/useSocket";

//每当有进程访问就会执行
onconnect = function(e: MessageEvent) {
  const port : MessagePort = e.ports[0]
  port.addEventListener("message", (e) => {
    const data = JSON.parse(e.data) as CustomSendMessage
    switch (data.type) {
      case 'connect':
        connectSocket(port, data.namespace, data.token)
        break
      case 'disconnect':
        disconnectSocket(port, data.namespace)
        break
    }
  });
  port.start()
}

// namespace : 实例
const sockets = new Map<string, {
  ports: MessagePort[],
  socket: Socket
}>()
function connectSocket(port : MessagePort, namespace: string, token: string) {
  const conf = sockets.get(namespace)
  if (conf) {
    conf.ports.push(port)
    if (conf.socket.disconnected) {
      conf.socket.connect()
    }
    return
  }
  const socket = io(namespace, {
    path: import.meta.env.VITE_SOCKET_PATH,
    transports: ["websocket"],
    autoConnect: true,
    closeOnBeforeunload: false,
    withCredentials: false,
    auth(cb) {
      cb({ token, })
    },
  })
  function broadcast(event: string, ...data: any[]) {
    const ports = sockets.get(namespace)?.ports
    if (ports) {
      for (const port of ports) {
        postMessage(port, {
          type: 'socket',
          event,
          data
        })
      }
    }
  }
  socket.onAny(broadcast)
  //onAny不会处理connect和disconnect
  socket.on('connect', (...data)=>broadcast('connect', ...data))
  socket.on('disconnect', (...data)=>broadcast('disconnect', ...data))
  sockets.set(namespace, {
    ports: [port],
    socket,
  })
}
function disconnectSocket(port: MessagePort, namespace: string) {
  const conf = sockets.get(namespace)
  if (conf) {
    const ports = conf.ports
    const index = ports.findIndex(p=>p===port)
    ports.splice(index, 1)
    if (ports.length === 0 && conf.socket.connected) {
      conf.socket.disconnect()
      sockets.delete(namespace)
    }
  }
}
function postMessage(port: MessagePort, message: CustomReceiveMessage) {
  port.postMessage(JSON.stringify(message))
}