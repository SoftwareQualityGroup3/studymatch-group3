import { useState } from 'react'
import { saveSelfAssessment } from '../api/students'

// as quatro competências, com o mesmo nome que o backend usa em cada campo
const COMPETENCIES = [
  { name: 'programming', label: 'Programação' },
  { name: 'mathematics', label: 'Matemática' },
  { name: 'databases', label: 'Bases de dados' },
  { name: 'softwareEngineering', label: 'Engenharia de software' },
]

const LEVELS = [1, 2, 3, 4, 5]

// valores de início: nenhuma competência escolhida
const EMPTY_VALUES = {
  programming: '',
  mathematics: '',
  databases: '',
  softwareEngineering: '',
}

// secção "Autoavaliação" do detalhe do estudante
// o estudante escolhe de 1 a 5 em cada competência e guarda
function SelfAssessmentSection({ studentId }) {
  const [values, setValues] = useState(EMPTY_VALUES)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const [message, setMessage] = useState('')

  // o backend pede as quatro notas, por isso só se guarda com todas escolhidas
  const complete = COMPETENCIES.every(({ name }) => values[name] !== '')

  function handleChange(name, value) {
    setValues((current) => ({ ...current, [name]: value }))
    // uma alteração nova apaga o aviso de "guardada"
    setMessage('')
  }

  async function handleSave(event) {
    event.preventDefault()
    setSaving(true)
    setError('')
    setMessage('')

    try {
      // os seletores guardam texto, por isso as notas passam a número
      const payload = Object.fromEntries(
        COMPETENCIES.map(({ name }) => [name, Number(values[name])])
      )
      await saveSelfAssessment(studentId, payload)
      setMessage('Autoavaliação guardada.')
      // avisa a secção do perfil para recarregar (mesmo evento do S2-06)
      window.dispatchEvent(
        new CustomEvent('student-profile-refresh', {
          detail: { studentId },
        })
      )
    } catch (e) {
      // as escolhas mantêm-se no ecrã para o estudante tentar outra vez
      setError(e.message || 'Não foi possível guardar a autoavaliação.')
    } finally {
      setSaving(false)
    }
  }

  return (
    <section data-testid="self-assessment">
      <h3>Autoavaliação</h3>

      <form onSubmit={handleSave}>
        {COMPETENCIES.map(({ name, label }) => (
          <div key={name}>
            <label htmlFor={`self-assessment-${name}`}>{label}</label>{' '}
            <select
              id={`self-assessment-${name}`}
              value={values[name]}
              onChange={(event) => handleChange(name, event.target.value)}
            >
              <option value="">Selecionar</option>
              {LEVELS.map((level) => (
                <option key={level} value={level}>
                  {level}
                </option>
              ))}
            </select>
          </div>
        ))}

        <button type="submit" disabled={!complete || saving}>
          {saving ? 'A guardar...' : 'Guardar autoavaliação'}
        </button>
      </form>

      {error && <p role="alert">{error}</p>}
      {message && <p role="status">{message}</p>}
    </section>
  )
}

export default SelfAssessmentSection
