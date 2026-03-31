import { defineConfig } from 'vite'

export default defineConfig({
	build: {
		outDir: '../wwwroot',
		emptyOutDir: true,
		manifest: true
	},
	server: {
		port: 5173,
		strictPort: true,
		proxy: {
			'/api': {
				target: 'http://localhost:7053',
				changeOrigin: true,
				secure: false,
			}
		}
	}
})
