window.onload = function () {


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
<div class="progress-bar">
    <div class="progress-fill"></div>
</div>
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
        const username = uploadForm.getElementsByTagName("input")[0].value;
        const password = uploadForm.getElementsByTagName("input")[1].value;
        const formData = new FormData();
        formData.append('username', username);
        formData.append('password', password);


        const xhr = new XMLHttpRequest();
        xhr.open('POST', '/spring/restclient/upload');

        filesToUpload.forEach((file, index) => {
            formData.append('files', file);

            xhr.upload.addEventListener('progress', e => {
                if (e.lengthComputable) {
                    const percent = (e.loaded / e.total) * 100;
                    const progressFill = fileList.children[index].querySelector('.progress-fill');
                    progressFill.style.width = percent + '%';
                }
            });

        });


        xhr.onload = () => {
            if (xhr.status === 200) {
                console.log(`File ${file.name} uploaded successfully`);
            } else {
                console.error(`Upload failed for ${file.name}`);
            }
        };

        xhr.send(formData);

    });

}
