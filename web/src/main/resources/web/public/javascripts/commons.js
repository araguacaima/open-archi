(function () {
    const Commons = (function () {
        const Commons = function (options) {

        };

        Object.defineProperty(Array.prototype, "pushAll", {
            value: function () {
                for (let i = 0; i < arguments.length; i++) {
                    const to_add = arguments[i];
                    if (to_add !== undefined) {
                        for (let n = 0; n < to_add.length; n += 300) {
                            push_apply(this, slice_call(to_add, n, n + 300));
                        }
                    }
                }
            }
        });

        Array.prototype.remove || (Array.prototype.remove = function (element) {
            const index = this.indexOf(element);
            if (index > -1) {
                this.splice(index, 1);
            }
        });

        Array.prototype.clone = function() {
            return this.slice(0);
        };

        const replaceAll = function (str, find, replace) {
            return str.replace(new RegExp(escapeRegExp(find), 'g'), replace);
        };

        const escapeRegExp = function (str) {
            return str.replace(/([.*+?^=!:${}()|\[\]\/\\])/g, "\\$1");
        };

        const push_apply = Function.apply.bind([].push);
        const slice_call = Function.call.bind([].slice);
        const isEmpty = function (o) {
            if (o === undefined || o === null) {
                return true;
            }
            if (isArray(o)) {
                return o.length === 0;
            } else {
                return Object.keys(o).length === 0 && o.constructor === Object
            }
        };
        const isArray = function (o) {
            return Object.prototype.toString.call(o) === '[object Array]';
        };
        const isObject = function (o) {
            return o !== null && !isArray(o) && typeof o === 'object';
        };
        const isScalar = function (o) {
            return (/number|boolean/).test(typeof o);
        };
        const isString = function (o) {
            return (/string/).test(typeof o);
        };
        const getObjectLength = function (object) {
            return Object.keys(object).length;
        };
        const traverse = function (x, firstStep, prefix) {
            const result = [];
            let innerResult;
            if (firstStep) {
                result[0] = "___id";
                innerResult = traverse(x, false, prefix);
                result.pushAll(innerResult);
            } else {
                if (isArray(x)) {
                    innerResult = traverseArray(x, prefix);
                    result.pushAll(innerResult);
                } else if (isObject(x)) {
                    if (x == "tag") {
                        console.log("1 " + x);
                    }
                    innerResult = traverseObject(x, prefix);
                    result.pushAll(innerResult);
                } else {
                    if (!x) {
                        if (prefix && prefix.endsWith('.')) {
                            result.push(prefix.substring(0, prefix.length - 1));
                        } else {
                            result.push(prefix);
                        }
                    } else if (!isObject(x)) {
                        if (prefix)
                            prefix = replaceAll(prefix, '[*]', '');
                        if (prefix.endsWith('.')) {
                            result.push(prefix.substring(0, prefix.length - 1));
                        } else {

                            result.push(prefix);
                        }
                    }

                }
            }
            return result;
        };

        const setValue = function (obj, path, value) {
            let schema = obj;
            const pList = path.split('.');
            const len = pList.length;
            for (let i = 0; i < len - 1; i++) {
                const elem = pList[i];
                if (!schema[elem]) {
                    schema[elem] = {}
                }
                schema = schema[elem];
            }
            schema[pList[len - 1]] = value.trim();
        };

        Commons.prototype = {
            isArray: isArray,
            isObject: isObject,
            traverse: traverse,
            setValue: setValue,
            replaceAll: replaceAll,
            isEmpty: isEmpty,
            isScalar: isScalar,
            isString: isString,
            getObjectLength: getObjectLength,
            findValues: findValues,
            clean: clean,
            random: random,
        };

        function traverseArray(arr, prefix) {
            const val = arr[0];
            let result = {};
            if (isArray(val)) {
                if (isObject(val)) {
                    result = traverseArray(val, prefix);
                }
            } else {
                result = traverse(val, false, prefix);
            }
            return result;
        }

        function traverseObject(obj, prefix) {
            const result = [];
            let innerResult = [];
            for (let key in obj) {
                if (obj.hasOwnProperty(key)) {
                    const innerObject = obj[key];
                    const isArray_ = isArray(innerObject);
                    const isObject_ = isObject(innerObject);
                    if (isArray_) {
                        innerResult = traverseArray(innerObject, !prefix ? (key + "[*].") : (prefix + key + "[*]."));
                        result.pushAll(innerResult);
                    } else if (isObject_) {
                        innerResult = traverse(innerObject, false, !prefix ? (key + ".") : (prefix + key + "."));
                        result.pushAll(innerResult);
                    } else if ("___id" !== key && "___s" !== key) {
                        if (prefix && prefix.endsWith('.')) {
                            result.push(prefix + key);
                        } else {
                            result.push(key);
                        }
                    }
                }
            }
            return result;
        }

        function findValues(obj, key) {
            return findValuesHelper(obj, key, []);
        }

        function clean(obj) {
            for (const propName in obj) {
                if (obj[propName] === null || obj[propName] === undefined) {
                    delete obj[propName];
                }
            }
            return obj;
        }

        function findValuesHelper(obj, key, list) {
            let i;
            if (!obj) return list;
            if (obj instanceof Array) {
                for (i in obj) {
                    list = list.concat(findValuesHelper(obj[i], key, []));
                }
                return list;
            }
            if (obj[key]) list.push(obj[key]);

            if ((typeof obj === "object") && (obj !== null)) {
                let children = Object.keys(obj);
                if (children.length > 0) {
                    for (i = 0; i < children.length; i++) {
                        list = list.concat(findValuesHelper(obj[children[i]], key, []));
                    }
                }
            }
            return list;
        }

        function random() {
            return (Math.random() + ' ').substring(2, 10) + (Math.random() + ' ').substring(2, 10);
        }

        return Commons;
    })();

    if (typeof module !== 'undefined' && typeof module.exports !== 'undefined') {
        module.exports = Commons;
    }
    else {
        if (typeof define === 'function' && define.amd) {
            define([], function () {
                return Commons;
            });
        }
        else {
            window.Commons = Commons;
        }
    }

})();

function toRGBString(rgbObject) {
    return "rgb(" + rgbObject.r + "," + rgbObject.g + "," + rgbObject.b + ")";
}

function getContrastYIQ(hexcolor) {
    if (hexcolor.startsWith("#")) {
        hexcolor = hexcolor.substr(1);
    }
    const r = parseInt(hexcolor.substr(0, 2), 16);
    const g = parseInt(hexcolor.substr(2, 2), 16);
    const b = parseInt(hexcolor.substr(4, 2), 16);
    const yiq = ((r * 299) + (g * 587) + (b * 114)) / 1000;
    return (yiq >= 128) ? 'black' : 'white';
}

function complementRBG(color) {
    let w3color_ = w3color(color);
    if (w3color_.valid) {
        return getContrastYIQ(w3color_.toHexString());
    } else {
        return 'black';
    }
}

function lighterColor(color) {
    let w3color_ = w3color(color);
    let hsl = w3color_.toHsl();
    hsl.l = 90;
    let hsl_ = w3color(parseHSL(hsl));
    const toHexString = hsl_.toHexString();
    return toHexString;
}

function darkerColor(color) {
    let w3color_ = w3color(color);
    let hsl = w3color_.toHsl();
    hsl.l = 10;
    let hsl_ = w3color(parseHSL(hsl));
    const toHexString = hsl_.toHexString();
    return toHexString;
}

function parseHSL(hsl) {
    return "hsl(" + hsl.h + "," + hsl.s + "," + hsl.l + "," + hsl.a + ")";
}
