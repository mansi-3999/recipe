import React, { useState, useRef } from 'react'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'

export default function Typeahead(){
  const [query, setQuery] = useState('')
  const [results, setResults] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const timer = useRef(null)
  const navigate = useNavigate()

  function onChange(e){
    const v = e.target.value
    setQuery(v)
    setResults([])
    if (timer.current) clearTimeout(timer.current)
    if (v && v.trim().length >= 3){
      timer.current = setTimeout(()=> fetchResults(v.trim()), 300)
    }
  }

  async function fetchResults(q){
    setLoading(true)
    setError(null)
    try{
  const res = await axios.get(`${import.meta.env.VITE_API_BASE_URL}/search?q=${encodeURIComponent(q)}`)
  console.log("Response data:", res.data);
      setResults(res.data || [])
    }catch(err){
      console.error(err)
      if (err.response && err.response.data && err.response.data.code) {
        setError(`Error (${err.response.status} - ${err.response.data.code}): ${err.response.data.message}`)
      } else {
        setError(err.response?.data?.message || 'Error searching recipes. Make sure to load recipes first.')
      }
      setResults([])
    }finally{
      setLoading(false)
    }
  }

  function onSelect(id){
    setQuery('')
    setResults([])
    navigate(`/recipes/${id}`)
  }

  return (
    <div className="typeahead">
      <input 
        value={query} 
        onChange={onChange} 
        placeholder="Search recipes by name or cuisine..."
        aria-label="Search recipes"
      />
      {loading && <div className="loader">Loading…</div>}
      {error && <div className="error">{error}</div>}
      {!loading && !error && results.length > 0 && (
        <ul className="dropdown">
          {results.map(r=> (
            <li key={r.id} onClick={()=> onSelect(r.id)}>
              <div className="title">{r.name}</div>
              <div className="meta">{r.cuisine || '—'}</div>
            </li>
          ))}
        </ul>
      )}
    </div>
  )
}
