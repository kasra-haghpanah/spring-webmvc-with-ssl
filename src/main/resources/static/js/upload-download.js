window.onload = function () {

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

    }).catch(err => {
        console.error(err);
    });


    html5.ajax({
        url: '/download/01K5K82CPCPEPMMDY2MR3VJXGZ',
        method: 'GET',
        headers: {
            'Accept-Language': 'fa'
            // 'Content-Type': 'application/json',
            //'Authorization': 'Bearer your-token'
        },
        body: null,// {name: 'Ali', age: 30},
        responseType: 'blob' // یا 'text', 'xml', 'base64', 'blob', 'arraybuffer'
    }).then(response => {
        console.log('Status:', response.status);
        console.log('Headers:', response.headers);
        //console.log('Body:', response.body);

        // responseType: 'blob'
        const url = URL.createObjectURL(response.body);
        /* <source src="video.mp4" type="video/mp4">*/
        var source = document.createElement("source");
        source.setAttribute("src", url);
        source.setAttribute("type", response.body.type);

        document.getElementById('myVideo').appendChild(source);

    }).catch(err => {
        console.error(err);
    });


    html5.ajax({
        url: '/customers?page=0&size=10',
        method: 'GET',
        headers: {
            'Accept-Language': 'fa'
            // 'Content-Type': 'application/json',
            //'Authorization': 'Bearer your-token'
        },
        body: null,// {name: 'Ali', age: 30},
        responseType: 'json' // یا 'text', 'xml', 'base64', 'blob', 'arraybuffer'
    }).then(response => {
        console.log('Status:', response.status);
        console.log('Headers:', response.headers);
        console.log('Body:', response.body);

    }).catch(err => {
        console.error(err);
    });


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
            fileItem.innerHTML = `<strong>${file.name}</strong>
<div class="content"></div>`;


            var contentType = file.type;
            var videoOrAudioElement = null;
            //var rsocketMessage = document.getElementById(item.id);

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
                videoOrAudioElement: null,//videoOrAudioElement,
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

        filesToUpload.forEach((file, index) => {
            formData.append('files', file);

        });

        xhr.upload.addEventListener('progress', e => {
            if (e.lengthComputable) {
                const percent = (e.loaded / e.total) * 100;
                const progressFill = uploadForm.querySelector('.progress-fill');
                progressFill.style.width = percent + '%';
            }
        });


        xhr.onload = () => {
            if (xhr.status === 200) {//xhr.responseText
                console.log(`File ${file.name} uploaded successfully`);
            } else {
                console.error(`Upload failed for ${file.name}`);
            }
        };

        xhr.send(formData);

    });

}
