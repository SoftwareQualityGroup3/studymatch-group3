import { useEffect, useState } from 'react'
import { getHealth } from '../api/health'

// este é o código do health que estava no app, só mudou de sítio
// para o app ficar só com as rotas
function HealthPage() {
  // resposta do backend com o estado da aplicação e da base de dados
  const [health, setHealth] = useState(null)
  // fica true até o backend responder
  const [loading, setLoading] = useState(true)
  // fica true se o pedido falhar
  const [error, setError] = useState(false)

  // corre uma vez quando a página abre e pergunta ao backend como está
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
    <section>
      <h2>Estado do sistema</h2>

      {/* só aparece enquanto espera pela resposta */}
      {loading && <p>A carregar...</p>}

      {/* só aparece se o pedido falhou */}
      {error && <p>Não foi possível obter o estado da aplicação.</p>}

      {/* só aparece se correu bem e já temos a resposta */}
      {!loading && !error && health && (
        <div>
          <p>Aplicação: {health.status}</p>
          <p>Base de dados: {health.database}</p>
        </div>
      )}
    </section>
  )
}

export default HealthPage
