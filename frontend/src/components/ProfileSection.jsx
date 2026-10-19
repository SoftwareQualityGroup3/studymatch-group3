import { useEffect, useState } from 'react'
import { getStudentProfile } from '../api/students'

// as quatro competências, com o mesmo nome que o backend usa no perfil (enum)
// a ordem desta lista é a ordem das linhas no ecrã
const COMPETENCIES = [
  { key: 'PROGRAMMING', label: 'Programação' },
  { key: 'MATHEMATICS', label: 'Matemática' },
  { key: 'DATABASES', label: 'Bases de dados' },
  { key: 'SOFTWARE_ENGINEERING', label: 'Engenharia de software' },
]

// texto para cada origem que o backend pode mandar
const SOURCE_LABELS = {
  ACADEMIC: 'Notas',
  SELF_ASSESSMENT: 'Autoavaliação',
  MIXED: 'Notas e autoavaliação',
}

// nome do evento que avisa que o perfil mudou
// quem registar um resultado (S2-06) ou uma autoavaliação (S2-11) dispara-o
const REFRESH_EVENT = 'student-profile-refresh'

// pontuação em texto
// null quer dizer que não há dados, por isso nunca se mostra como 0
function formatScore(score) {
  if (score === null || score === undefined) return 'Sem dados'
  return `${score.toFixed(2)} / 20`
}

// origem em texto
// sem pontuação não há origem para mostrar
function formatSource(result) {
  if (!result || result.score === null || result.score === undefined) return '—'
  return SOURCE_LABELS[result.source] ?? result.source
}

// secção "Perfil" do detalhe do estudante
// mostra por competência a pontuação, o número de evidências e a origem
function ProfileSection({ studentId }) {
  // sobe um número sempre que o perfil tem de ser pedido outra vez
  const [reloads, setReloads] = useState(0)
  // guarda também a chave a que o resultado pertence (estudante + recarga)
  // assim nunca se mostra um perfil velho como se fosse o atual
  const [result, setResult] = useState({ key: null, profile: null, error: '' })

  // chave do pedido que devia estar no ecrã neste momento
  const key = `${studentId}:${reloads}`

  // ouve o aviso de que o perfil mudou e pede-o outra vez
  useEffect(() => {
    function handleRefresh(event) {
      // ignora avisos que sejam de outro estudante
      if (Number(event.detail?.studentId) !== Number(studentId)) return
      setReloads((current) => current + 1)
    }

    window.addEventListener(REFRESH_EVENT, handleRefresh)

    return () => {
      window.removeEventListener(REFRESH_EVENT, handleRefresh)
    }
  }, [studentId])

  // vai buscar o perfil ao backend quando o estudante muda ou há uma recarga
  useEffect(() => {
    // fica true se a chave mudar antes da resposta chegar
    // sem isto uma resposta atrasada podia aparecer por cima do perfil novo
    let ignore = false

    async function loadProfile() {
      try {
        const profile = await getStudentProfile(studentId)
        if (!ignore) setResult({ key, profile, error: '' })
      } catch {
        if (ignore) return
        setResult({
          key,
          profile: null,
          error: 'Não foi possível carregar o perfil. Verifique se o backend está ligado.',
        })
      }
    }

    loadProfile()

    return () => {
      ignore = true
    }
  }, [studentId, key])

  // enquanto o resultado guardado não for deste pedido, ainda estamos a carregar
  const loading = result.key !== key
  // o backend manda as competências dentro de "profiles", indexadas pelo nome
  const profiles = result.profile?.profiles ?? {}

  return (
    <section data-testid="profile">
      <h3>Perfil</h3>

      {/* só aparece enquanto espera pela resposta */}
      {loading && <p>A carregar...</p>}

      {/* só aparece se não conseguiu carregar o perfil */}
      {!loading && result.error && <p role="alert">{result.error}</p>}

      {/* só aparece depois de o perfil chegar */}
      {!loading && result.profile && (
        <table>
          <thead>
          <tr>
            <th>Competência</th>
            <th>Pontuação</th>
            <th>Evidências</th>
            <th>Origem</th>
          </tr>
          </thead>
          <tbody>
          {/* uma linha por competência, mesmo as que ainda não têm dados */}
          {COMPETENCIES.map(({ key: name, label }) => {
            const competency = profiles[name]

            return (
              <tr key={name}>
                <td>{label}</td>
                <td>{formatScore(competency?.score)}</td>
                <td>{competency?.evidenceCount ?? 0}</td>
                <td>{formatSource(competency)}</td>
              </tr>
            )
          })}
          </tbody>
        </table>
      )}
    </section>
  )
}

export default ProfileSection
