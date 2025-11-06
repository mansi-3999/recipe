import React from 'react'
import { render, screen, waitFor } from '@testing-library/react'
import { BrowserRouter, useParams } from 'react-router-dom'
import axios from 'axios'
import RecipeDetails from '../RecipeDetails'

jest.mock('axios')
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useParams: jest.fn()
}))

describe('RecipeDetails', () => {
  beforeEach(() => {
    jest.clearAllMocks()
  })

  it('should show loading state initially', () => {
    useParams.mockReturnValue({ id: '1' })
    axios.get.mockImplementation(() => new Promise(resolve => setTimeout(resolve, 100)))

    render(
      <BrowserRouter>
        <RecipeDetails />
      </BrowserRouter>
    )

    expect(screen.getByText(/loading/i)).toBeInTheDocument()
  })

  it('should render recipe details after successful fetch', async () => {
    useParams.mockReturnValue({ id: '1' })
    const mockRecipe = {
      id: 1,
      name: 'Test Recipe',
      cuisine: 'Test Cuisine',
      description: 'Test Description',
      image: 'test.jpg'
    }

    axios.get.mockResolvedValueOnce({ data: mockRecipe })

    render(
      <BrowserRouter>
        <RecipeDetails />
      </BrowserRouter>
    )

    await waitFor(() => {
      expect(screen.getByText('Test Recipe')).toBeInTheDocument()
      expect(screen.getByText(/test cuisine/i)).toBeInTheDocument()
      expect(screen.getByText('Test Description')).toBeInTheDocument()
      expect(screen.getByAltText('Test Recipe')).toHaveAttribute('src', 'test.jpg')
    })
  })

  it('should show error state when recipe not found', async () => {
    useParams.mockReturnValue({ id: '999' })
    axios.get.mockRejectedValueOnce(new Error('Not found'))

    render(
      <BrowserRouter>
        <RecipeDetails />
      </BrowserRouter>
    )

    await waitFor(() => {
      expect(screen.getByText('Not found')).toBeInTheDocument()
      expect(screen.getByText('Back to Home')).toBeInTheDocument()
    })
  })
})