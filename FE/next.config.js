/** @type {import('next').NextConfig} */
// const withBundleAnalyzer = require("@next/bundle-analyzer")({
//   enabled: true,
//   openAnalyzer: true,
// });
// module.exports = withBundleAnalyzer({
//   webpack(config, options) {
//     return config;
//   },
//   reactStrictMode: false,
//   swcMinify: true,
//   output: "standalone",
//   images: {
//     remotePatterns: [
//       {
//         protocol: "https",
//         hostname: "picsum.photos",
//         port: "",
//         pathname: "/**",
//       },
//       {
//         protocol: "https",
//         hostname: "cdn.thecolumnist.kr",
//         port: "",
//         pathname: "/**",
//       },
//       {
//         protocol: "https",
//         hostname: "cdn.newspenguin.com",
//         port: "",
//         pathname: "/**",
//       },
//       {
//         protocol: "https",
//         hostname: "aucation-bucket.s3.ap-northeast-2.amazonaws.com",
//         port: "",
//         pathname: "/**",
//       },
//     ],
//   },
//   async rewrites() {
//     return [
//       {
//         source: "/:path*",
//         destination: "/:path*",
//       },
//       {
//         source: "/api/v1/:path*",
//         destination: `${process.env.NEXT_PUBLIC_SERVER_URL}/api/v1/:path*`,
//       },
//     ];
//   },
// });
const nextConfig = {
  reactStrictMode: false,
  swcMinify: true,
  output: "standalone",
  images: {
    domains: [],
    remotePatterns: [
      {
        protocol: "https",
        hostname: "picsum.photos",
        port: "",
        pathname: "/**",
      },
      {
        protocol: "https",
        hostname: "cdn.thecolumnist.kr",
        port: "",
        pathname: "/**",
      },
      {
        protocol: "https",
        hostname: "cdn.newspenguin.com",
        port: "",
        pathname: "/**",
      },
      {
        protocol: "https",
        hostname: "aucation-bucket.s3.ap-northeast-2.amazonaws.com",
        port: "",
        pathname: "/**",
      },
    ],
  },
  async rewrites() {
    return [
      {
        source: "/:path*",
        destination: "/:path*",
      },
      {
        source: "/api/v1/:path*",
        destination: `${process.env.NEXT_PUBLIC_SERVER_URL}/api/v1/:path*`,
      },
      {
        source: "/api/v2/:path*",
        destination: `${process.env.NEXT_PUBLIC_CHAT_SERVER_URL}/api/v2/:path*`,
      },
    ];
  },
};

module.exports = nextConfig;
