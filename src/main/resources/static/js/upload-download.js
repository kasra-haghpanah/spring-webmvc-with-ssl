window.onload = function () {

    function lazyLoadForDownload(e) {
        var node = e.target;
        while (node.getAttribute("fileId") == null) {
            node = node.parentNode;
        }
        /*        if (node.getAttribute("disable") == "true") {
                    return;
                }*/
        var fileId = node.getAttribute("fileId");
        var name = node.innerText;
        //node.innerText = "";
        //node.setAttribute("disable", "true");
        download(fileId, name, node);

        node.removeEventListener("click", lazyLoadForDownload);
        //alert(fileId);
    }

    function download(fileId, fileName, parent) {

        html5.ajax({
            url: `/download/${fileId}`,
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
            /* <source src="video.mp4" type="video/mp4">*/
            var source = document.createElement("source");
            source.setAttribute("src", url);
            source.setAttribute("type", response.body.type);

            //document.getElementById('myVideo').appendChild(source);

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
            url: `/files?file=${ownerId}`,
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

            var fileBox = document.getElementById("fileBox");

            while (fileBox.firstChild) {
                fileBox.firstChild.removeEventListener("click", lazyLoadForDownload);
                fileBox.removeChild(fileBox.firstChild);
            }

            for (var i = 0; i < response.body.length; i++) {
                var file = response.body[i];
                var child = document.createElement("div");
                //child.innerText = file.name;
                child.innerHTML = `<div class="progress-container">
    <div class="progress-bar"></div>
    <p class="progress-text">0%</p>
</div>
<div>${file.name}</div>`;
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

    function getCustomers(page, size) {
        html5.ajax({
            url: `/customers?page=${page}&size=${size}`,
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
                    if (e.target.tagName == 'TD') {
                        element = element.parentNode;
                    }
                    var id = element.getElementsByTagName("td")[0].innerText;
                    getFileByOwnerId(id);
                })
                tr.innerHTML = `<td>${customer.id}</td><td>${customer.firstName}</td><td>${customer.lastName}</td><td>${customer.phoneNumber}</td>`;
                table.appendChild(tr);
            }

        }).catch(error => {
            console.error(error);
        });

    }

    html5.ajax({
        url: '/login',
        method: 'POST',
        headers: {
            'Accept-Language': 'fa',
            'Content-Type': 'application/json'//,
            //'Authorization': 'Bearer your-token'
        },
        body: {
            "username": "kasra_khpk1985@yahoo.com",
            "password": "123"
        },
        responseType: 'json' // یا 'text', 'xml', 'base64', 'blob', 'arraybuffer'
    }).then(response => {
        console.log('Status:', response.status);
        console.log('Headers:', response.headers);
        console.log('Body:', response.body);

    }).catch(error => {
        console.error(error);
    });


    getCustomers(0, 60);

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
            fileItem.innerHTML = `<div class="close-button">&nbsp;</div></div><strong>${file.name}</strong><div class="content"></div>`;

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
        const firstName = uploadForm.getElementsByTagName("input")[0];
        const lastName = uploadForm.getElementsByTagName("input")[1];
        const phoneNumber = uploadForm.getElementsByTagName("input")[2];


        const formData = new FormData();
        formData.append(firstName.name, firstName.value);
        formData.append(lastName.name, lastName.value);
        formData.append(phoneNumber.name, phoneNumber.value);

        const xhr = new XMLHttpRequest();
        xhr.open('POST', '/spring/restclient/upload');
        xhr.setRequestHeader("Accept-Language", "fa");

        filesToUpload.forEach((file, index) => {
            formData.append('files', file);

        });

        xhr.upload.addEventListener('progress', e => {
            if (e.lengthComputable) {
                const percent = Math.floor((e.loaded / e.total) * 100);
                const progressFill = uploadForm.querySelector('.progress-fill');

                var progressBar = uploadForm.querySelector(".progress-bar");
                var progressText = uploadForm.querySelector(".progress-text");

                progressText.innerText = `${percent}% (${e.loaded}/${e.total})`;
                progressBar.style.width = percent + "%";


                //progressFill.style.width = percent + '%';
            }
        });


        xhr.onload = () => {
            var response = xhr.responseText;
            if (xhr.status === 200) {
                alert(response);
                getCustomers(0, 60);
                //console.log(`File ${file.name} uploaded successfully`);
            } else {
                var res = JSON.parse(response);
                alert(res['errors']['unexpected']);
                //console.error(`Upload failed for ${file.name}`);
            }
        };

        xhr.send(formData);

    });

}
