window.onload = function () {
    //const socket = new WebSocket(`wss://localhost:8080/ws?token=${jwtToken}`);

    (function () {

        if (window.ws === undefined) {
            window.ws = {
                socket: null,
                reconnectAttempts: 0,
                reconnectInterval: 5000,
                intervalId: null,
                scrollToEnd: function () {
                    let messagesDiv = document.getElementById("messages");
                    messagesDiv.scrollTop = messagesDiv.scrollHeight;
                },
                clearSchedule: function () {
                    if (this.intervalId != null) {
                        clearInterval(this.intervalId);
                        this.intervalId = null;
                    }
                },
                onOpen: function () {
                    ws.clearSchedule();
                    ws.reconnectAttempts = 0;
                    ws.logMessage("✅ اتصال برقرار شد");
                    ws.scrollToEnd();
                },
                onMessage: function (event) {
                    try {
                        let response = JSON.parse(event.data);
                        let icon = response.type === "sender" ? "📤 ارسال: " : "📩 دریافت: ";
                        ws.logMessage(icon + response.email + ": " + response.message);
                        ws.scrollToEnd();
                    } catch (e) {
                        ws.logMessage("⚠️ خطا در پردازش پیام: " + e.message);
                        ws.scrollToEnd();
                    }
                },
                onClose: function () {

                    try {
                        ws.socket.removeEventListener("open", ws.onOpen);
                        ws.socket.removeEventListener("message", ws.onMessage);
                        ws.socket.removeEventListener("close", ws.onClose);
                        ws.socket.removeEventListener("error", ws.onError);
                        ws.socket = null;
                    } catch (e) {
                        console.log(e);
                    }

                    ws.logMessage("❌ اتصال بسته شد");
                    ws.scrollToEnd();
                    ws.attemptReconnect();
                },
                onError: function (error) {
                    ws.logMessage("⚠️ خطا: " + error.message);
                    ws.scrollToEnd();

                    try {
                        if (ws.socket != null) {
                            ws.socket.close();
                        }
                    } catch (e) {
                        console.log(e);
                    }
                },
                connectWebSocket: function () {
                    try {
                        this.socket = new WebSocket(`wss://localhost:8443/spring/ws`);
                        this.socket.addEventListener("open", this.onOpen);
                        this.socket.addEventListener("message", this.onMessage);
                        this.socket.addEventListener("close", this.onClose);
                        this.socket.addEventListener("error", this.onError);
                    } catch (e) {
                        console.log(e);
                    }
                },
                attemptReconnect: function () {
                    this.clearSchedule();
                    this.reconnectAttempts++;
                    this.logMessage(`🔄 تلاش برای اتصال مجدد (${this.reconnectAttempts})...`);
                    this.scrollToEnd();
                    this.intervalId = setInterval(() => {
                        this.connectWebSocket();
                    }, this.reconnectInterval);
                },
                sendMessage: function () {
                    const input = document.getElementById("messageInput");
                    const msg = input.value;
                    this.socket.send(msg);
                    input.value = "";
                },
                logMessage: function (msg) {
                    let messagesDiv = document.getElementById("messages");
                    const p = document.createElement("p");
                    p.textContent = msg;
                    messagesDiv.appendChild(p);
                    this.scrollToEnd();
                },
                refreshToken: function () {
                    html5.ajax({
                        url: `/spring/refresh/token`,
                        method: 'POST',
                        headers: {
                            'Accept-Language': 'fa',
                            'Content-Type': 'application/json'//,
                            //'Authorization': 'Bearer your-token'
                        },
                        body: null,
                        responseType: 'json' // یا 'text', 'xml', 'base64', 'blob', 'arraybuffer'
                    }).then(response => {
                        //console.log('Status:', response.status);
                        //console.log('Headers:', response.headers);
                        //console.log('Body:', response.body);

                    }).catch(error => {
                        console.error(error);
                    });
                }


            }


        }

    })();

    ws.connectWebSocket();// شروع اتصال
    setInterval(ws.refreshToken, 14 * 60000);

}