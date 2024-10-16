function checkLength(el, maxLength){
    if(el.value.length > maxLength){
        el.value = el.value.substring(0, maxLength);
    }
}

function checkBlank(elementId){
    return $("#" + elementId).val().trim().length !== 0;
}