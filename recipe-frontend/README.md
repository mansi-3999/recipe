# Recipe Frontend (Vite + React)

This is a minimal single-page app that provides a typeahead search box to query the backend API and a details page that renders the recipe image.

Quick start (local):

1. Install dependencies

   npm install

2. Run the dev server

   npm run dev

The Vite dev server proxies `/api` to `http://localhost:8080` (see `vite.config.js`). Ensure the backend is running on port 8080.

Features implemented:
- Typeahead search after 3 characters (debounced 300ms)
- Dropdown shows `id`, `name`, `cuisine`
- Clicking an item navigates to details page and renders recipe image
- Responsive layout and basic lazy-loading of route components (can be extended)

