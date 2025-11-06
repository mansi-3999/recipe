import React from 'react'
import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import { BrowserRouter } from 'react-router-dom'
import axios from 'axios'
import Typeahead from '../Typeahead'

jest.mock('axios')

describe('Typeahead', () => {
  beforeEach(() => {
    jest.clearAllMocks()
  })

  it('should render search input', () => {
    render(
      <BrowserRouter>
        <Typeahead />
      </BrowserRouter>
    )
    expect(screen.getByPlaceholderText(/search recipes/i)).toBeInTheDocument()
  })

  it('should show loading state while fetching results', async () => {
    axios.get.mockImplementationOnce(() => new Promise(resolve => setTimeout(resolve, 100)))

    render(
      <BrowserRouter>
        <Typeahead />
      </BrowserRouter>
    )

    const input = screen.getByPlaceholderText(/search recipes/i)
    fireEvent.change(input, { target: { value: 'pasta' } })

    await waitFor(() => {
      expect(screen.getByText(/loading/i)).toBeInTheDocument()
    })
  })

  it('should show results after successful fetch', async () => {
    const mockResults = [
      { id: 1, name: 'Pasta Carbonara', cuisine: 'Italian' },
      { id: 2, name: 'Pizza', cuisine: 'Italian' }
    ]

    axios.get.mockResolvedValueOnce({ data: mockResults })

    render(
      <BrowserRouter>
        <Typeahead />
      </BrowserRouter>
    )

    const input = screen.getByPlaceholderText(/search recipes/i)
    fireEvent.change(input, { target: { value: 'pasta' } })

    await waitFor(() => {
      expect(screen.getByText('Pasta Carbonara')).toBeInTheDocument()
      expect(screen.getByText('Pizza')).toBeInTheDocument()
    })
  })
})