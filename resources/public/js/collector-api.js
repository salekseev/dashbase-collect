function processInsert(e) {
    if (e.preventDefault) e.preventDefault();
    var name = $("#insertTableName").val();
    var insertData = $("#insertData").val();
    var isBatch = $("#insertMultiple").is(':checked');
    var url = "/collect/" + name + "?isBatch=" + isBatch;
    $.ajax({
        type : 'POST',
        url : url,
        data : insertData,
        dataType : 'json',
        contentType: "application/json",
        encode : true
    }).done(function(data) {
        // data is
        //
        // {status: "ok"}
        // everything is good
        //
        // {status: "missing_email"}
        // if email is empty
        console.log(data);
    });

    return false;
}

var insertForm = document.getElementById("insertForm");
if (insertForm.attachEvent) {
    insertForm.attachEvent("submit", processInsert);
} else {
    insertForm.addEventListener("submit", processInsert);
}

function processUpload(e) {
    if (e.preventDefault) e.preventDefault();
    var name = $("#uploadName").val();
	var data = new FormData($("#uploadFile"));
	$.each(jQuery('#uploadFile')[0].files, function(i, file) {
	    data.append('file-'+i, file);
	});	

    var url = "/upload/" + name;
    $.ajax({
        type : 'POST',
        url : url,
        data : data,
        cache: false,
		contentType: false,
		processData: false
    }).done(function(data) {
        // data is
        //
        // {status: "ok"}
        // everything is good
        //
        // {status: "missing_email"}
        // if email is empty
        console.log(data);
    });
    return false;
}

var uploadForm = document.getElementById("uploadForm");
if (uploadForm.attachEvent) {
    uploadForm.attachEvent("submit", processUpload);
} else {
    uploadForm.addEventListener("submit", processUpload);
}