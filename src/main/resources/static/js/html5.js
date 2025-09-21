(function () {

    if (typeof window.html5 === 'undefined') {
        window.html5 = {

            ajax: function ({
                                url,
                                method = 'GET',
                                headers = {},
                                body = null,
                                responseType = 'text',
                                onProgress = null
                            }) {
                return new Promise((resolve, reject) => {
                    const xhr = new XMLHttpRequest();
                    xhr.open(method, url, true);

                    // تنظیم هدرها
                    for (const key in headers) {
                        xhr.setRequestHeader(key, headers[key]);
                    }

                    // نوع پاسخ
                    xhr.responseType = responseType === 'base64' ? 'arraybuffer' : responseType;

                    // رویداد پیشرفت دانلود
                    if (typeof onProgress === 'function') {
                        xhr.onprogress = function (event) {
                            if (event.lengthComputable) {
                                const percent = (event.loaded / event.total) * 100;
                                onProgress({loaded: event.loaded, total: event.total, percent});
                            }
                        };
                    }

                    xhr.onload = function () {
                        const responseHeaders = {};
                        const rawHeaders = xhr.getAllResponseHeaders().split('\r\n');
                        rawHeaders.forEach(line => {
                            const [key, value] = line.split(': ');
                            if (key) responseHeaders[key.toLowerCase()] = value;
                        });

                        let responseBody = xhr.response;
                        if (responseType === 'base64') {
                            const binary = String.fromCharCode(...new Uint8Array(xhr.response));
                            responseBody = btoa(binary);
                        }

                        resolve({
                            status: xhr.status,
                            statusText: xhr.statusText,
                            headers: responseHeaders,
                            body: responseBody
                        });
                    };

                    xhr.onerror = function () {
                        reject(new Error('Network error'));
                    };

                    xhr.send(body && typeof body === 'object' && !(body instanceof FormData)
                        ? JSON.stringify(body)
                        : body);
                });
            },

            uploadFormData: function ({
                                          method = 'POST',
                                          formElement,
                                          files,
                                          endpoint,
                                          headers = {},
                                          onProgress,
                                          onSuccess,
                                          onError
                                      }) {
                if (!formElement || !files || !files.length || !endpoint) {
                    throw new Error("پارامترهای ورودی ناقص هستند.");
                }

                const inputs = formElement.querySelectorAll("input[name]");
                const formData = new FormData();

                inputs.forEach(input => {
                    formData.append(input.name, input.value);
                });

                files.forEach(file => {
                    formData.append("files", file);
                });

                return new Promise((resolve, reject) => {
                    const xhr = new XMLHttpRequest();
                    xhr.open(method, endpoint, true);

                    // تنظیم هدرها
                    Object.entries(headers).forEach(([key, value]) => {
                        xhr.setRequestHeader(key, value);
                    });

                    // پیشرفت آپلود
                    xhr.upload.addEventListener("progress", e => {
                        if (e.lengthComputable && typeof onProgress === "function") {
                            const percent = Math.floor((e.loaded / e.total) * 100);
                            onProgress({percent, loaded: e.loaded, total: e.total});
                        }
                    });

                    // پاسخ موفق
                    xhr.onload = () => {
                        if (xhr.status === 200) {
                            resolve(xhr.responseText);
                            if (typeof onSuccess === "function") onSuccess(xhr.responseText);
                        } else {
                            try {
                                const res = JSON.parse(xhr.responseText);
                                reject(res.errors?.unexpected || "خطای سرور");
                                if (typeof onError === "function") onError(res);
                            } catch {
                                reject("پاسخ نامعتبر از سرور");
                            }
                        }
                    };

                    // خطای شبکه
                    xhr.onerror = () => {
                        reject("خطا در ارتباط با سرور");
                        if (typeof onError === "function") onError("network");
                    };

                    xhr.send(formData);
                });
            },

            toBlob: function (buffer, contentType) {
                if (buffer.constructor === Blob) {
                    return buffer;
                } else if (buffer.constructor === ArrayBuffer) {
                    buffer = new Uint8Array(buffer);
                } else if (buffer.constructor === String) {
                    try {
                        buffer = this.arrayBufferToString(buffer);  //this.base64ToBinary(buffer);
                    } catch (e) {
                        buffer = this.base64ToBinary(buffer);
                    }

                }
                return new Blob([buffer], {type: contentType});
            },

            arrayBufferToBase64: function (buffer) {
                var binary = '';
                var bytes = new Uint8Array(buffer);
                var len = bytes.byteLength;
                for (var i = 0; i < len; i++) {
                    binary += String.fromCharCode(bytes[i]);
                }
                return window.btoa(binary);
            },

            base64ToUnicode: function (sBase64) {
                return decodeURIComponent(atob(sBase64).split('').map(function (c) {
                    return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
                }).join(''));
            },

            createLink: function (blob, fileName, title) {
                //const blob = new Blob(["سلام دنیا!"], {type: "text/plain"});
                // ایجاد URL موقت برای Blob
                const url = URL.createObjectURL(blob);

                // ساختن عنصر <a> برای دانلود
                const a = document.createElement("a");
                a.href = url;
                a.download = fileName; // نام فایل هنگام دانلود
                a.textContent = title;

                // اضافه کردن به صفحه
                return a;
            },

            createFileElement({
                                  filename,
                                  contentType,
                                  content: rawContent,
                                  width = null,
                                  height = null,
                                  class: className = null,
                                  videoOrAudioElement = null,
                                  isDecodeHTMLCode = true
                              }) {

                let content = rawContent;
                let isBase64 = typeof content === "string";
                let isArrayBuffer = content instanceof ArrayBuffer;
                let isBlob = content instanceof Blob;
                let isDataUri = isBase64 && content.includes(";base64,");

                if (content instanceof Uint8Array) {
                    content = content.buffer;
                    isArrayBuffer = true;
                }

                if (isArrayBuffer || content instanceof File) {
                    content = this.toBlob(content, contentType);
                    isBlob = true;
                    isBase64 = false;
                }

                if (contentType == null || content == null) {
                    return null;
                }

                const typeLower = contentType.toLowerCase();

                if (!typeLower.includes("text")) {
                    if (!isDataUri && isBase64) {
                        content = `data:${contentType};base64,${content}`;
                    }

                    if (content !== "") {
                        if (isBase64 || isArrayBuffer) {
                            content = URL.createObjectURL(this.toBlob(content, contentType));
                        } else if (isBlob) {
                            content = URL.createObjectURL(content);
                        }
                    }
                }

                if (typeLower.includes("pdf")) {
                    return this.createIframe(content, width, height, className);
                }

                if (typeLower.includes("text")) {
                    return this.createTextElement(content, contentType, isBlob, isDecodeHTMLCode, width, height, className);
                }

                if (typeLower.includes("video") || typeLower.includes("audio")) {
                    return this.createMediaElement(content, contentType, typeLower.includes("audio") ? "audio" : "video", width, height, className, videoOrAudioElement);
                }

                if (typeLower.startsWith("image/")) {
                    return this.createImageElement(content, filename, width, height, className);
                }

                return null;
            },

            createIframe(src, width, height, className) {
                const iframe = document.createElement("iframe");
                iframe.src = src;
                iframe.type = "application/pdf";
                iframe.alt = "pdf";
                if (width) iframe.width = width;
                if (height) iframe.height = height;
                if (className) iframe.className = className;
                return iframe;
            },

            createTextElement(content, contentType, isBlob, decodeHTML, width, height, className) {
                const div = document.createElement("div");

                const applyContent = (text) => {
                    if (contentType.toLowerCase() === "text/html" && decodeHTML) {
                        text = HTML5.stripHtml(text);
                    }
                    div.innerHTML = text;
                };

                if (isBlob) {
                    content.text().then(applyContent);
                } else {
                    if (decodeHTML) content = HTML5.stripHtml(content);
                    div.innerHTML = content;
                }

                if (className) div.className = className;
                if (width || height) {
                    const style = [
                        "resize: both",
                        "overflow-x: scroll",
                        "overflow-y: scroll",
                        width ? `width: ${width}px` : "",
                        height ? `height: ${height}` : ""
                    ].filter(Boolean).join(";");
                    div.style = style;
                }

                return div;
            },

            createMediaElement(src, contentType, tag, width, height, className, existingElement) {
                const media = existingElement || document.createElement(tag);
                media.src = src;
                media.preload = "metadata";
                media.controls = true;
                media.type = contentType;

                if (className) media.className = className;
                if (width || (height && tag === "video")) {
                    const style = [
                        width ? `width: ${width}` : "",
                        tag === "video" && height ? `height: ${height}` : ""
                    ].filter(Boolean).join(";");
                    media.style = style;
                }

                return media;
            },

            createImageElement(src, alt, width, height, className) {
                const img = document.createElement("img");
                img.src = src;
                img.alt = alt;
                if (className) img.className = className;
                if (width || height) {
                    const style = [
                        width ? `width: ${width}px` : "",
                        height ? `height: ${height}` : ""
                    ].filter(Boolean).join(";");
                    img.style = style;
                }
                return img;
            }


        };
    }


})();