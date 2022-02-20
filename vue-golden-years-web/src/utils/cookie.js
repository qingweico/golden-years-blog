/**
 * Cookie常用的一些工具类
 */

export function setCookie(name, value, days) {

    let date = new Date();
    date.setDate(date.getDate() + days);
    document.cookie = name + '=' + value + ';expires=' + date;

    let domain, domainParts, expires, host;

    if (days) {
        date = new Date();
        date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
        expires = "; expires=" + date.toGMTString();
    } else {
        expires = "";
    }

    host = location.host;
    if (host.split('.').length === 1) {
        // no "." in a domain - it's localhost or something similar
        document.cookie = name + "=" + value + expires + "; path=/";
    } else {
        // Remember the cookie on all subdomains.
        // Start with trying to set cookie to the top domain.
        // (example: if user is on foo.com, try to set
        //  cookie to domain ".com")
        //
        // If the cookie will not be set, it means ".com"
        // is a top level domain and we need to
        // set the cookie to ".foo.com"

        domainParts = host.split('.');
        domainParts.shift();
        domain = '.' + domainParts.join('.');

        document.cookie = name + "=" + value + expires + "; path=/; domain=" + domain;

        // check if cookie was successfully set to the given domain
        // (otherwise it was a Top-Level Domain)
        if (getCookie(name) == null || getCookie(name) !== value) {
            // append "." to current domain
            domain = '.' + host;
            document.cookie = name + "=" + value + expires + "; path=/; domain=" + domain;
        }
    }
}

export function getCookie(name) {
    let reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)");
    let arr = document.cookie.match(reg);
    if (arr)
        return unescape(arr[2])
    else
        return null
}

export function delCookie(name) {
    let exp = new Date();
    exp.setTime(exp.getTime() - 1);
    let cookieVal = getCookie(name);
    if (cookieVal != null)
        document.cookie = name + "=" + cookieVal + ";expires=" + exp.toGMTString();
}
