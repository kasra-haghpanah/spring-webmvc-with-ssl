(function () {

    if (typeof window.html5 === 'undefined') {
        window.html5 = {

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

            createFileElement: function (data) {

                /*                data = {
                                    filename: filename,
                                    contentType: contentType,
                                    content: content,
                                    width: width,
                                    height: height,
                                    class: "col-7",
                                    videoOrAudioElement: video,
                                    isDecodeHTMLCode: true
                                }*/
                // var isBase64 = (data.isBase64 === undefined || data.isBase64 == null) ? false : true;

                var isDecodeHTMLCode = (data.isDecodeHTMLCode === undefined || data.isDecodeHTMLCode == null) ? false : true;
                var isBase64 = data.content.constructor === String;
                if (data.content.constructor === Uint8Array) {
                    data.content = data.content.buffer;
                }
                var isArrayBuffer = data.content.constructor === ArrayBuffer;
                if (isArrayBuffer || data.content.constructor === File) {
                    data.content = this.toBlob(data.content, data.contentType);
                    isArrayBuffer = false;
                }

                var isBlob = data.content.constructor === Blob;

                var isDataUri = isBase64 && data.content.indexOf(";Base64,")
                if (isArrayBuffer || isBlob) {
                    isBase64 = false;
                }


                var filename = data.filename;
                var contentType = data.contentType;
                var content = data.content;

                if (contentType.indexOf("text") > -1) {
                    if (isArrayBuffer) {
                        content = this.arrayBufferToBase64(content);
                    } else if (isBase64) {
                        content = this.base64ToUnicode(content);
                    }

                }

                var width = data.width === undefined ? null : data.width;
                var height = data.height === undefined ? null : data.height;
                var className = data.class === undefined ? null : data.class;
                var video = data.videoOrAudioElement === undefined ? null : data.videoOrAudioElement;


                //var td = element.target || element.srcElement || element.currentTarget;
                if (contentType == null || content == null) {
                    reject("");
                }
                var contentTypeLower = contentType.toLowerCase();


                if (contentTypeLower.indexOf("text") == -1) {
                    if (isDataUri == -1 && isBase64) {
                        content = "data:" + contentType + ";base64," + content;
                    }
                    if (content != "") {
                        if (isArrayBuffer || isBase64) {
                            content = window.URL.createObjectURL(this.toBlob(content, contentType));
                        } else if (isBlob) {
                            content = window.URL.createObjectURL(content);
                        }

                    }

                }

                if (contentTypeLower.indexOf("pdf") > -1) {
                    var embed = document.createElement("iframe");
                    embed.setAttribute("src", content);//window.URL.createObjectURL(item.content)
                    embed.setAttribute("type", "application/pdf");
                    embed.setAttribute("alt", "pdf");
                    if (width != null) {
                        embed.setAttribute("width", width);
                    }
                    if (height != null) {
                        embed.setAttribute("height", height);
                    }

                    if (className != null) {
                        embed.className = className;
                    }
                    //embed.setAttribute("pluginspage", "http://www.adobe.com/products/acrobat/readstep2.html");
                    return embed;

                } else if (contentTypeLower.indexOf("text") > -1) {

                    var div = document.createElement("div");
                    if (isBlob) {
                        content.text()
                            .then(function (text) {
                                if (contentType.toLowerCase() == "text/html" && isDecodeHTMLCode) {
                                    text = HTML5.stripHtml(text);
                                }
                                div.innerHTML = text;
                            })
                    } else {
                        if (isDecodeHTMLCode) {
                            content = HTML5.stripHtml(content);
                        }
                        div.innerHTML = content;
                    }
                    //https://developer.mozilla.org/en-US/docs/Web/API/WindowBase64/Base64_encoding_and_decoding
                    //atob(item.content);  //ajax.b64DecodeUnicode(item.content); //atob(item.content);     //btoa encode to base64     //atob decode base64 to string

                    if (className != null) {
                        div.className = className;
                    }

                    if (width != null || height != null) {
                        var letWidth = width != null ? "width: " + width + "px" : "";
                        var letHeight = height != null ? "height: " + height : "";
                        if (letWidth != "") {
                            letWidth += ";";
                        }
                        div.setAttribute("style", "resize: both;overflow-x: scroll;overflow-y: scroll;" + letWidth + letHeight);
                    }
                    return div;
                } else if (contentTypeLower.indexOf("video") > -1 || contentTypeLower.indexOf("audio") > -1) {
                    var type = "video";
                    if (contentTypeLower.indexOf("audio") > -1) {
                        type = "audio";
                    }

                    video = video == null ? document.createElement(type) : video;
                    video.src = null;
                    video.src = content; //window.URL.createObjectURL(item.content);
                    //video.play();
                    video.preload = "metadata";
                    video.controls = "controls";
                    video.type = contentType;

                    if (className != null) {
                        video.className = className;
                    }

                    if (width != null || height != null) {
                        var letWidth = width != null ? "width: " + width : "";
                        var letHeight = height != null ? "height: " + height : "";
                        if (type = "audio") {
                            letHeight = "";
                        }
                        if (letWidth != "") {
                            letWidth += ";";
                        }
                        video.setAttribute("style", letWidth + letHeight);
                    }
                    //video.size = "150";
                    //video.height = "150";
                    return video;
                } else if (contentTypeLower.indexOf("image/") > -1) {
                    var img = document.createElement("img");
                    img.src = content;//window.URL.createObjectURL(item.content);
                    //img.size = "150";
                    //img.height = "150";
                    img.alt = filename;
                    if (className != null) {
                        img.className = className;
                    }

                    if (width != null || height != null) {
                        var letWidth = width != null ? "width: " + width + "px" : "";
                        var letHeight = height != null ? "height: " + height : "";
                        if (letWidth != "") {
                            letWidth += ";";
                        }
                        img.setAttribute("style", letWidth + letHeight);
                    }

                    return img;
                }


            }


        };
    }


})();