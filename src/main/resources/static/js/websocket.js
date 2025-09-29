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
                connectWebSocket: function () {
                    try {
                        ws.socket = new WebSocket(`wss://localhost:8443/spring/ws`);
                    } catch (e) {
                        console.log(e);
                    }

                    this.socket.onopen = () => {
                        ws.clearSchedule();
                        ws.reconnectAttempts = 0;
                        ws.logMessage("✅ اتصال برقرار شد");
                        ws.scrollToEnd();
                    };

                    this.socket.onmessage = (event) => {
                        try {
                            let response = JSON.parse(event.data);
                            let icon = response.type === "sender" ? "📤 ارسال: " : "📩 دریافت: ";
                            ws.logMessage(icon + response.email + ": " + response.message);
                            ws.scrollToEnd();
                        } catch (e) {
                            ws.logMessage("⚠️ خطا در پردازش پیام: " + e.message);
                            ws.scrollToEnd();
                        }
                    };

                    this.socket.onclose = () => {
                        ws.logMessage("❌ اتصال بسته شد");
                        ws.scrollToEnd();
                        ws.attemptReconnect();
                    };

                    this.socket.onerror = (error) => {
                        ws.logMessage("⚠️ خطا: " + error.message);
                        ws.scrollToEnd();
                        ws.socket.close(); // بستن دستی برای فعال‌سازی onclose
                        ws.socket = null;
                    };
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
                    //logMessage("📤 ارسال: " + msg);
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