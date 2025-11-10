import React, { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import axios from 'axios'

export default function RecipeDetails(){
  const { id } = useParams()
  const [recipe, setRecipe] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(()=>{
    let mounted = true
    setError(null)
  axios.get(`${import.meta.env.VITE_API_BASE_URL}/${id}`).then(r=>{
      if(mounted) setRecipe(r.data)
    }).catch((err)=>{
      if(mounted) {
        setError(err.response?.data?.message || err.message || 'An error occurred while fetching the recipe')
        setRecipe(null)
      }
    }).finally(()=> mounted && setLoading(false))
    return ()=> mounted = false
  },[id])

  if (loading) return <div className="content">Loading…</div>
  if (error) return (
    <div className="content error">
      <div className="error-message">{error}</div>
      <Link to="/">Back to Home</Link>
    </div>
  )
  if (!recipe) return <div className="content">Recipe not found. <Link to="/">Back</Link></div>

  return (
    <div className="content details">
      <Link to="/" className="back-link">← Back</Link>
      
      <div className="image-column">
        {recipe.image && <img src={recipe.image} alt={recipe.name} className="recipe-image"/>}
      </div>

      <div className="content-column">
        <div>
          <h2>{recipe.name}</h2>
          <div className="meta-rows">
            <div className="meta-row">
              <div><strong>Cuisine: </strong>{recipe.cuisine || '—'}</div>
              {recipe.difficulty && <div><strong>Difficulty: </strong>{recipe.difficulty}</div>}
              {recipe.servings && <div><strong>Servings: </strong>{recipe.servings}</div>}
              {recipe.caloriesPerServing && (
                <div><strong>Calories: </strong>{recipe.caloriesPerServing} per serving</div>
              )}
            </div>
            
            <div className="meta-row timing">
              {recipe.prepTimeMinutes && (
                <div><strong>Prep Time: </strong>{recipe.prepTimeMinutes} minutes</div>
              )}
              {recipe.cookTimeMinutes && (
                <div><strong>Cook Time: </strong>{recipe.cookTimeMinutes} minutes</div>
              )}
              {recipe.prepTimeMinutes && recipe.cookTimeMinutes && (
                <div><strong>Total Time: </strong>{recipe.prepTimeMinutes + recipe.cookTimeMinutes} minutes</div>
              )}
            </div>
          </div>
        </div>

        {recipe.description && <div className="description">{recipe.description}</div>}

        {recipe.ingredients && Array.isArray(recipe.ingredients) && (
          <div className="ingredients">
            <h3>Ingredients</h3>
            <ul>{recipe.ingredients.map((ing, i) => <li key={i}>{ing}</li>)}</ul>
          </div>
        )}

        {/* Instructions: can be a string or an array of steps */}
        {recipe.instructions && (
          <div className="instructions">
            <h3>Instructions</h3>
            {Array.isArray(recipe.instructions)
              ? <ol>{recipe.instructions.map((s, i) => <li key={i}>{s}</li>)}</ol>
              : <div>{recipe.instructions}</div>
            }
          </div>
        )}
      </div>
    </div>
  )
}
