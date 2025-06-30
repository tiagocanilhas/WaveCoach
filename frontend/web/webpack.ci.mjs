import { server } from "typescript";

export default {
    mode: 'development',
    entry: {
      main: './src/index.ci.tsx'
    },
    devServer: {
      historyApiFallback: true,
      port: 3000,
    },
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
                namedExport: false,
              },
            },
          },
        ],
      },
      {
        test: /\.css$/,
        exclude: /\.module\.css$/, 
        use: ['style-loader', 'css-loader'],
      },
    ],
  },
  };