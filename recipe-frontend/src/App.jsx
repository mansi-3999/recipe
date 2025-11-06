import React, { Suspense, lazy } from 'react'
import Typeahead from './components/Typeahead'

export default function App(){
  return (
    <div className="app">
      <header className="header">
        <h1>Recipes</h1>
        <Typeahead/>
      </header>
      <main className="content">
        <p>Use the search box above to find recipes by name or cuisine. Click a result to view details.</p>
      </main>
    </div>
  )
}
