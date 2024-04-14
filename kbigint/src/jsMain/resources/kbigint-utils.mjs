// noinspection JSUnusedGlobalSymbols

/**
 * @private
 * @param {bigint} n
 * @param {bigint} k
 * @returns {bigint}
 * @see {@link https://stackoverflow.com/a/53684036/21974435}
 */
function newtonRoot(n, k) {
    const x = ((n / k) + k) >> 1n;
    return k === x || k === (x - 1n) ? k : newtonRoot(n, x);
}
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
 * Raise the value to the `n`-th power.
 *
 * @param {bigint} value
 * @param {number} n
 * @returns {bigint}
 */
export function pow(value, n) {
    return value ** BigInt(n);
}
