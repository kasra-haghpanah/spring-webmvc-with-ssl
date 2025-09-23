window.onload = function () {


    function refreshToken() {
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
            console.log('Body:', response.body);

        }).catch(error => {
            console.error(error);
        });
    }

    function removeUploadPreviewElement(e) {
        var btn = e.target;
        var fileItem = btn;
        while (fileItem.className != "file-item") {
            fileItem = fileItem.parentNode;
        }
        var children = fileItem.parentNode.querySelectorAll(".file-item");
        var index = 0;
        for (var i = 0; i < children.length; i++) {
            if (children[i] == fileItem) {
                index = i;
                break;
            }
        }

        filesToUpload.splice(index, 1);
        fileItem.parentNode.removeChild(fileItem);
        btn.removeEventListener("click", removeUploadPreviewElement);
    }

    function lazyLoadForDownload(e) {
        var node = e.target;
        while (node.getAttribute("fileId") == null) {
            node = node.parentNode;
        }
        /*        if (node.getAttribute("disable") == "true") {
                    return;
                }*/
        var fileId = node.getAttribute("fileId");
        var name = node.lastChild.innerText;
        download(fileId, name, node);

        node.removeEventListener("click", lazyLoadForDownload);
        //alert(fileId);
    }

    function download(fileId, fileName, parent) {

        html5.ajax({
            url: `/spring/download/${fileId}`,
            method: 'GET',
            headers: {
                'Accept-Language': 'fa'
                // 'Content-Type': 'application/json',
                //'Authorization': 'Bearer your-token'
            },
            body: null,// {name: 'Ali', age: 30},
            responseType: 'blob', // یا 'text', 'xml', 'base64', 'blob', 'arraybuffer'
            onProgress: function ({loaded, total, percent}) {
                var progressBar = parent.querySelector(".progress-bar");
                var progressText = parent.querySelector(".progress-text");

                progressText.innerText = `${percent.toFixed(0)}% (${loaded}/${total})`;
                progressBar.style.width = percent.toFixed(0) + "%";
            }
        }).then(response => {
            //console.log('Status:', response.status);
            //console.log('Headers:', response.headers);
            //console.log('Body:', response.body);


            // responseType: 'blob'
            const url = URL.createObjectURL(response.body);

            var element = html5.createFileElement({
                filename: fileName,
                contentType: response.body.type,
                content: response.body,
                class: 'child',
                //width: width,
                //height: "400px",
                videoOrAudioElement: null,
                isDecodeHTMLCode: true
            });

            var a = html5.createLink(response.body, fileName, fileName);
            a.style.color = "white";
            a.style.marginLeft = "10px";
            a.style.cursor = "pointer";

            var divLink = document.createElement("div");
            divLink.style.background = "black";
            divLink.style.width = "100%";
            divLink.style.height = "50px";


            divLink.appendChild(a);
            parent.appendChild(divLink);
            parent.style.cursor = "none";

            if (typeof element !== 'undefined') {
                parent.appendChild(element);
            }


        }).catch(error => {
            console.error(error);
        });

    }

    function getFileByOwnerId(ownerId) {
        html5.ajax({
            url: `/spring/files?file=${ownerId}`,
            method: 'GET',
            headers: {
                'Accept-Language': 'fa'
                // 'Content-Type': 'application/json',
                //'Authorization': 'Bearer your-token'
            },
            body: null,// {name: 'Ali', age: 30},
            responseType: 'json' // یا 'text', 'xml', 'base64', 'blob', 'arraybuffer'
        }).then(response => {
            //console.log('Status:', response.status);
            //console.log('Headers:', response.headers);
            //console.log('Body:', response.body);

            clearFileBox();

            for (var i = 0; i < response.body.length; i++) {
                var file = response.body[i];
                var child = document.createElement("div");
                //child.innerText = file.name;
                child.innerHTML = `<div class="progress-container"><div class="progress-bar"></div><div class="progress-text">0%</div></div><div>${file.name}</div>`;
                child.setAttribute("type", file.type);
                child.setAttribute("fileId", file.id);
                child.className = 'child';
                fileBox.appendChild(child);

                child.addEventListener("click", lazyLoadForDownload);


            }

        }).catch(error => {
            console.error(error);
        });
    }

    function deleteCustomerById(id, rowElement) {
        html5.ajax({
            url: `/spring/delete/customer?id=${id}`,
            method: 'DELETE',
            headers: {
                'Accept-Language': 'fa'
                // 'Content-Type': 'application/json',
                //'Authorization': 'Bearer your-token'
            },
            body: null,// {name: 'Ali', age: 30},
            responseType: 'json' // یا 'text', 'xml', 'base64', 'blob', 'arraybuffer'
        }).then(response => {
            //console.log('Status:', response.status);
            //console.log('Headers:', response.headers);
            //console.log('Body:', response.body);
            clearFileBox();
            var closeBtn = rowElement.querySelector(".close-btn");
            closeBtn.removeEventListener("click", deleteCustomerEvent);
            rowElement.parentNode.removeChild(rowElement);

        }).catch(error => {
            console.error(error);
        });
    }

    function getCustomers(page, size) {
        html5.ajax({
            url: `/spring/customers?page=${page}&size=${size}`,
            method: 'GET',
            headers: {
                'Accept-Language': 'fa'
                // 'Content-Type': 'application/json',
                //'Authorization': 'Bearer your-token'
            },
            body: null,// {name: 'Ali', age: 30},
            responseType: 'json' // یا 'text', 'xml', 'base64', 'blob', 'arraybuffer'
        }).then(response => {
            //console.log('Status:', response.status);
            //console.log('Headers:', response.headers);
            //console.log('Body:', response.body);


            var table = document.getElementsByTagName("table")[0];

            var trList = table.getElementsByTagName("tr");
            while (trList.length > 1) {
                var trList = table.getElementsByTagName("tr");
                table.removeChild(trList[trList.length - 1]);
            }

            for (var i = 0; i < response.body.length; i++) {
                var customer = response.body[i];
                var tr = document.createElement("tr");
                tr.style.cursor = "pointer";
                tr.addEventListener("click", e => {
                    var element = e.target;
                    if (e.target.tagName == 'BUTTON') {
                        return;
                    }
                    if (e.target.tagName == 'TD') {
                        element = element.parentNode;
                    }
                    var id = element.getElementsByTagName("td")[0].innerText;
                    getFileByOwnerId(id);
                })
                tr.innerHTML = `<td>${customer.id}</td><td>${customer.firstName}</td><td>${customer.lastName}</td><td>${customer.phoneNumber}</td><button class="close-btn" style="z-index: 1;">&times;</button>`;
                table.appendChild(tr);

                var closeBtn = tr.querySelector(".close-btn");
                closeBtn.addEventListener("click", deleteCustomerEvent);
            }

        }).catch(error => {
            console.error(error);
        });

    }

    function clearFileBox() {
        var fileBox = document.getElementById("fileBox");

        while (fileBox.firstChild) {
            fileBox.firstChild.removeEventListener("click", lazyLoadForDownload);
            fileBox.removeChild(fileBox.firstChild);
        }
    }

    function deleteCustomerEvent(e) {
        var closeButton = e.target;
        var tr = closeButton;
        while (tr.tagName != 'TR') {
            tr = tr.parentNode;
        }
        var id = tr.firstChild.innerText;
        deleteCustomerById(id, tr);
    }

    getCustomers(0, 60);

    //setInterval(refreshToken, 5000);
    setInterval(refreshToken, 14 * 60000);

    const dropZone = document.getElementById('dropZone');
    const fileInput = document.getElementById('fileInput');
    const fileList = document.getElementById('fileList');
    const uploadForm = document.getElementById('uploadForm');
    const button = document.getElementById('send');

    let filesToUpload = [];

    dropZone.addEventListener('click', () => fileInput.click());

    dropZone.addEventListener('dragover', e => {
        e.preventDefault();
        dropZone.style.background = '#f0f0f0';
    });

    dropZone.addEventListener('dragleave', () => {
        dropZone.style.background = '';
    });

    dropZone.addEventListener('drop', e => {
        e.preventDefault();
        dropZone.style.background = '';
        handleFiles(e.dataTransfer.files);
    });

    fileInput.addEventListener('change', e => {
        handleFiles(e.target.files);
    });

    function handleFiles(files) {
        for (let file of files) {
            filesToUpload.push(file);
            const fileItem = document.createElement('div');
            fileItem.className = 'file-item';
            fileItem.innerHTML = `<button class="close-btn">&times;</button></div><strong>${file.name}</strong><div class="content"></div>`;

            var button = fileItem.getElementsByTagName("button")[0];

            button.addEventListener("click", removeUploadPreviewElement);//ggggggggggggggggggggggg

            var contentType = file.type;

            var type = contentType.toLowerCase().indexOf("video") > -1 ? "video" : contentType;
            if (type != "video") {
                type = contentType.toLowerCase().indexOf("audio") > -1 ? "audio" : contentType;
            }
            // code

            var element = html5.createFileElement({
                filename: file.name,
                contentType: contentType,
                content: file,
                class: 'col-7',
                height: "400px",
                videoOrAudioElement: null,
                isDecodeHTMLCode: true
            });


            var divTags = fileItem.getElementsByTagName("div");
            var lastDiv = divTags[divTags.length - 1];

            if (typeof element !== "undefined") {
                lastDiv.appendChild(element);
            }

            fileList.appendChild(fileItem);


        }
    }

    button.addEventListener('click', e => {
        e.preventDefault();

        try {
            html5.uploadFormData({
                method: 'POST',
                formElement: uploadForm,
                files: filesToUpload,
                endpoint: "/spring/restclient/upload",
                headers: {"Accept-Language": "fa"},
                onProgress: ({percent, loaded, total}) => {
                    var progressBar = uploadForm.querySelector(".progress-bar");
                    var progressText = uploadForm.querySelector(".progress-text");
                    progressText.innerText = `${percent}% (${loaded}/${total})`;
                    progressBar.style.width = Math.floor(percent) + "%";
                },
                onSuccess: response => {
                    getCustomers(0, 60);
                    alert(response);

                },
                onError: errors => {

                    for (error in errors['errors']) {
                        alert(errors['errors'][error]);
                    }

                    //alert("❌ خطا: " + err);
                }
            });
        } catch (errors) {
            alert(errors)
        }


    });

}
