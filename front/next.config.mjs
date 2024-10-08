/** @type {import('next').NextConfig} */
const nextConfig = {
    // output: 'export',
    /**
     * Error: Image Optimization using the default loader is not compatible with `{ output: 'export' }`.
     *   Possible solutions:
     *     - Remove `{ output: 'export' }` and run "next start" to run server mode including the Image Optimization API.
     *     - Configure `{ images: { unoptimized: true } }` in `next.config.js` to disable the Image Optimization API.
     *   Read more: https://nextjs.org/docs/messages/export-image-api
     */
    images: {
        unoptimized: true
    }
};

export default nextConfig;
