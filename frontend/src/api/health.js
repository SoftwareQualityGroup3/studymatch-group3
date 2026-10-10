export async function getHealth() {
  const response = await fetch('/api/health')

  const data = await response.json()

  if (!response.ok && response.status !== 503) {
    throw new Error('Erro ao obter o estado da aplicação')
  }

  return data
}
