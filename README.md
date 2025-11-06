# Recipe Search Application

A full-stack application that provides recipe search with typeahead functionality. The backend fetches and indexes recipe data from an external API, while the frontend provides a responsive search interface with recipe details view.

## Project Structure

- `recipe/` - Spring Boot backend
- `recipe-frontend/` - React frontend

## Backend (Spring Boot)

### Prerequisites

- JDK 21
- Maven 3.8+

### Features

- Load recipes from external API (https://dummyjson.com/recipes)
- In-memory H2 database for storage
- Hibernate Search with Lucene for full-text search
- REST API endpoints for search and retrieval
- Resilient external API calls with Spring Retry
- OpenAPI documentation
- Comprehensive test suite

### Build and Run

```powershell
cd recipe

# Build
mvn clean package

# Run
mvn spring-boot:run
```

The backend will start on http://localhost:8080

### API Endpoints

- `POST /api/recipes/load` - Load recipes from external API
- `GET /api/recipes/search?q={query}` - Search recipes by name or cuisine
- `GET /api/recipes/{id}` - Get recipe details by ID

OpenAPI documentation available at: http://localhost:8080/swagger-ui.html

## Frontend (React + Vite)

### Prerequisites

- Node.js 16+
- npm 7+

### Features

- Responsive design
- Typeahead search (debounced, min 3 chars)
- Recipe details view with image
- React Router for navigation
- Comprehensive test suite
- Lazy-loaded routes

### Build and Run

```powershell
cd recipe-frontend

# Install dependencies
npm install

# Run development server
npm run dev

# Run tests
npm test

# Run tests with coverage
npm run test:coverage
```

The frontend will start on http://localhost:5173

### Development

The frontend uses Vite's development server with proxy configuration to forward API requests to the backend. The proxy is configured in `vite.config.js`.

## Testing

Both backend and frontend have comprehensive test suites:

### Backend Tests

- Unit tests for entities and services
- Integration tests with H2
- Controller tests with MockMvc
- Coverage reports generated during build

### Frontend Tests

- Component tests with React Testing Library
- Integration tests for API calls
- Coverage reporting available

## Architecture Notes

1. Backend
   - Layered architecture (controller -> service -> repository)
   - Uses Hibernate Search for efficient full-text search
   - H2 in-memory database for quick setup
   - Spring Retry for resilient external API calls

2. Frontend
   - React SPA with responsive design
   - Modular component structure
   - Efficient search with debouncing
   - Clean separation of concerns

## Configuration

### Backend

Key properties in `application.properties`:
- Database configuration
- Hibernate Search settings
- External API URL
- Logging levels

### Frontend

Environment variables (if needed) can be added to `.env` files:
- `VITE_API_URL` - Backend API URL (defaults to proxy)

## Security Notes

1. Backend
   - Input validation on API endpoints
   - Safe external API handling
   - Prepared statements for DB queries

2. Frontend
   - Input sanitization
   - XSS prevention
   - Secure API calls

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit changes
4. Push to the branch
5. Create a Pull Request

## License

MIT