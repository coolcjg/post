function checkLength(el, maxLength){
    if(el.value.length > maxLength){
        el.value = el.value.substring(0, maxLength);
    }
}