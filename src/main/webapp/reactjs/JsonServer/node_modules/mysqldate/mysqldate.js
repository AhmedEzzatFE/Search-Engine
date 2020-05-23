Date.prototype.toMysqlUTCDate = function() {
    function td(number) {
        if (number < 10) return '0' + number;
        return number;
    }
    return this.getUTCFullYear() + '-' + td(this.getUTCMonth() + 1) + '-' + td(this.getUTCDate()) +
        " " + td(this.getUTCHours()) + ":" + td(this.getUTCMinutes()) + ":" + td(this.getUTCSeconds());
};

Date.prototype.toMysqlDate = function() {
    function td(number) {
        if (number < 10) return '0' + number;
        return number;
    }
    return this.getFullYear() + '-' + td(this.getMonth() + 1) + '-' + td(this.getDate()) +
        " " + td(this.getHours()) + ":" + td(this.getMinutes()) + ":" + td(this.getSeconds());
};

Date.fromMysqlDate = function(timestamp) {
    if (Date.parse(timestamp))
        return (new Date(timestamp));
    else {
        var date = timestamp.split(/[\s-:]/);
        return (new Date(date[0], date[1], date[2], date[3], date[4], date[5]));
    }
};

Date.fromMysqlUTCDate = function(timestamp) {
    var date = timestamp.split(/[\s-:]/);
    return (new Date(Date.UTC(date[0], date[1]-1, date[2], date[3], date[4], date[5])));
};
