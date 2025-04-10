export default {
  mode: 'development',
  resolve: {
    extensions: ['.js', '.ts', '.tsx', '.css'],
  },
  plugins: [],
  module: {
    rules: [
      {
        test: /\.tsx?$/,
        use: ['ts-loader'],
        exclude: /node_modules/,
      },
      {
        test: /\.module\.css$/,
        use: [
          'style-loader',
          {
            loader: 'css-loader',
            options: {
              modules: {
                namedExport: false
              }
            }
          }
        ]
      },
    ],
  },
  devServer: {
    historyApiFallback: true,
    port: 3000,
    compress: false,
    proxy: [
      {
        context: ['/api'],
        target: 'http://localhost:8080',
        onProxyRes: (proxyRes, req, res) => {
          proxyRes.on('close', () => {
            if (!res.writableEnded) 
              res.end();
          });

          res.on('close', () => {
            proxyRes.destroy();
          });
        },
      },
    ]
  }
};
