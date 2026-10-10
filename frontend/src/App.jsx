import { useEffect, useState } from 'react'
import { getHealth } from './api/health'

function App() {
  const [health, setHealth] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(false)

  useEffect(() => {
    async function loadHealth() {
      try {
        const data = await getHealth()
        setHealth(data)
      } catch {
        setError(true)
      } finally {
        setLoading(false)
      }
    }

    loadHealth()
  }, [])

  return (
    <main>
      <h1>StudyMatch</h1>
      <h2>Estado do sistema</h2>

      {loading && <p>A carregar...</p>}

      {error && (
        <p>Não foi possível obter o estado da aplicação.</p>
      )}

      {!loading && !error && health && (
        <div>
          <p>Aplicação: {health.status}</p>
          <p>Base de dados: {health.database}</p>
        </div>
      )}
    </main>
  )
}

export default App
