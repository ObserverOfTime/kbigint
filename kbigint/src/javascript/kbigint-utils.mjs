// noinspection JSUnusedGlobalSymbols

/**
 * Return the sign of the value.
 *
 * @param {bigint} value
 * @returns {-1|0|1}
 */
export function sign(value) {
    return cmp(value, 0n);
}

/**
 * Count the total number of bits in the value.
 *
 * @param {bigint} value
 * @returns {number}
 */
export function bitLength(value) {
    if (value < 0n) value = ~value;
    if (value === 0n) return 0;

    let low = 0n, high = 1n;

    while ((value >> high) !== 0n) high <<= 1n;

    while (high - low > 1n) {
        const mid = (low + high) >> 1n;
        if ((value >> mid) !== 0n) low = mid;
        else high = mid;
    }

    return Number(low) + 1;
}

/**
 * Count the number of set bits in the value.
 *
 * @param {bigint} value
 * @returns {number}
 * @see https://stackoverflow.com/a/72518920/21974435
 */
export function bitCount(value) {
    let n = (value >= 0n ? value : ~value), bits;
    for (bits = 0; n > 0n; ++bits) n &= n - 1n;
    return bits
}

/**
 * Return the absolute value.
 *
 * @param {bigint} value
 * @returns {bigint}
 */
export function abs(value) {
    return value < 0n ? -value : value;
}

/**
 * Compute the approximate square root of the value.
 *
 * @param {bigint} value
 * @returns {bigint}
 */
export function sqrt(value) {
    if (value < 2n) return value;
    if (value >= 16n) newtonRoot(value, 1n);
    return BigInt(Math.sqrt(Number(value)) | 0);
}

/**
 * Compare the two values.
 *
 * @param {bigint} a
 * @param {bigint} b
 * @returns {-1|0|1}
 */
export function cmp(a, b) {
    return a === b ? 0 : a < b ? -1 : 1;
}

/**
 * Find the Greatest Common Divisor of the two values.
 *
 * @param {bigint} a
 * @param {bigint} b
 * @returns {bigint}
 */
export function gcd(a, b) {
    if (b === 0n) return a;
    return gcd(b, a % b);
}

/**
 * Raise the value to the `n`-th power.
 *
 * @param {bigint} value
 * @param {number} n
 * @returns {bigint}
 */
export function pow(value, n) {
    return value ** BigInt(n);
}

/**
 * Compute the base `b` integer logarithm of the value.
 *
 * @param {bigint} value
 * @param {number} b
 * @returns {number}
 */
export function log(value, b) {
    const guess = ((bitLength(value) - 1) / Math.log2(b)) | 0;
    const base = BigInt(b)
    const lowerBound = base ** BigInt(guess);

    if (lowerBound > value) return guess - 1;
    if (lowerBound * base <= value) return guess + 1;
    return guess;
}

/**
 * Convert a {@link BigInt} to an {@link Int8Array}.
 *
 * @param {bigint} value
 * @returns {Int8Array}
 * @see https://coolaj86.com/articles/convert-js-bigints-to-typedarrays/
 */
export function toByteArray(value) {
    let hex = bnToHex(value);
    if (hex.length & 1) hex = '0' + hex;
    const len = hex.length >> 1;
    const arr = new Int8Array(len);
    for (let i = 0, j = 0; i < len; i += 1, j += 2) {
        arr[i] = parseInt(hex.slice(j, j + 2), 16);
    }
    return arr;
}

/**
 * Convert a numeric {@link Array} to a {@link BigInt}.
 *
 * @param {number[] | ArrayLike<number>} bytes
 * @returns {bigint}
 * @see https://coolaj86.com/articles/convert-js-bigints-to-typedarrays/
 */
export function fromByteArray(bytes) {
    const hex = new Array(bytes.length);
    Uint8Array.from(bytes).forEach(n => {
        const h = n.toString(16);
        hex.push(h.length & 1 ? '0' + h : h);
    });
    return hexToBn(hex.join(''));
}

/**
 * @private
 * @param {bigint} n
 * @param {bigint} k
 * @returns {bigint}
 * @see https://stackoverflow.com/a/53684036/21974435
 */
function newtonRoot(n, k) {
    const x = ((n / k) + k) >> 1n;
    return k === x || k === (x - 1n) ? k : newtonRoot(n, x);
}

/**
 * @private
 * @param {string} str
 * @returns {string}
 */
function bitFlip(str) {
    return Array.from(str, i => i === '0' ? '1' : '0').join('')
}

/**
 * @private
 * @param {bigint} bn
 * @returns {bigint}
 * @see https://coolaj86.com/articles/convert-decimal-to-hex-with-js-bigints/
 */
function bitNot(bn) {
    let bin = (-bn).toString(2)
    while (bin.length % 8) bin = '0' + bin;
    const prefix = bin[0] === '1' && bin.slice(1).includes('1')
        ? '11111111' : ''
    return BigInt('0b' + prefix + bitFlip(bin)) + 1n;
}

/**
 * @private
 * @param {string} hex
 * @returns {number}
 */
function highByte(hex) {
    return parseInt(hex.slice(0, 2), 16);
}

/**
 * @private
 * @param {string} hex
 * @returns {bigint}
 * @see https://coolaj86.com/articles/convert-hex-to-decimal-with-js-bigints/
 */
function hexToBn(hex) {
    if (hex.length & 1) hex = '0' + hex;
    let bn = BigInt('0x' + hex);
    if (highByte(hex) & 128) {
        // manually perform two's complement (flip bits, add one)
        // (because JS binary operators are incorrect for negatives)
        bn = -(BigInt('0b' + bitFlip(bn.toString(2))) + 1n);
    }
    return bn;
}

/**
 * @private
 * @param {bigint} bn
 * @returns {string}
 * @see https://coolaj86.com/articles/convert-decimal-to-hex-with-js-bigints/
 */
function bnToHex(bn) {
    const pos = bn >= 0n;
    let hex = (pos ? bn : bitNot(bn)).toString(16);
    if (hex.length & 1) hex = '0' + hex;
    if (pos && (highByte(hex) & 128)) hex = '00' + hex;
    return hex;
}
