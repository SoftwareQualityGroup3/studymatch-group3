import { useState } from 'react'

// value é o que vai para o servidor e label é o que o utilizador vê
export const STUDENT_STATUSES = [
  { value: 'ACTIVE', label: 'Ativo' },
  { value: 'SUSPENDED', label: 'Suspenso' },
  { value: 'COMPLETED', label: 'Concluído' },
]

// valores de início quando é um estudante novo
const EMPTY_STUDENT = {
  studentNumber: '',
  name: '',
  email: '',
  course: '',
  studyPlan: '',
  enrollmentYear: '',
  curricularYear: '',
  status: 'ACTIVE',
}

// lista dos campos do formulário
// cada um vira uma label e uma caixa de texto lá em baixo
const FIELDS = [
  { name: 'studentNumber', label: 'Número de estudante', type: 'text' },
  { name: 'name', label: 'Nome', type: 'text' },
  { name: 'email', label: 'Contacto institucional (email)', type: 'email' },
  { name: 'course', label: 'Curso', type: 'text' },
  { name: 'studyPlan', label: 'Plano de estudos', type: 'text' },
  { name: 'enrollmentYear', label: 'Ano letivo de ingresso', type: 'number' },
  { name: 'curricularYear', label: 'Ano curricular', type: 'number' },
]

// passa os dados de um estudante para os valores do formulário
function toFormValues(student) {
  // sem estudante é criar por isso fica tudo vazio
  if (!student) return EMPTY_STUDENT

  // com estudante é editar por isso copia os dados dele
  // o que faltar fica com texto vazio
  return Object.fromEntries(
    Object.keys(EMPTY_STUDENT).map((key) => [key, student[key] ?? ''])
  )
}

// prepara os valores para mandar ao backend
function toPayload(values) {
  return {
    ...values, // copia tudo
    // as caixas guardam texto por isso os anos passam a número
    enrollmentYear: values.enrollmentYear === '' ? null : Number(values.enrollmentYear),
    curricularYear: values.curricularYear === '' ? null : Number(values.curricularYear),
  }
}

// o mesmo formulário serve para criar e editar
// recebe o estudante (só se for editar) o texto do botão e a função que corre ao enviar
function StudentForm({ initialStudent, submitLabel, onSubmit }) {
  // se veio um estudante é porque estamos a editar
  const isEditing = Boolean(initialStudent)

  // o que está escrito em cada caixa
  const [values, setValues] = useState(() => toFormValues(initialStudent))
  // fica true enquanto o pedido está a decorrer
  const [submitting, setSubmitting] = useState(false)
  // erro geral do formulário
  const [formError, setFormError] = useState('')
  // erros de cada campo
  const [fieldErrors, setFieldErrors] = useState({})

  // corre sempre que se escreve numa caixa
  function handleChange(event) {
    // name é o campo que mudou e value o texto novo
    const { name, value } = event.target
    // copia os valores antigos e muda só o campo certo
    setValues((current) => ({ ...current, [name]: value }))
  }

  // corre quando se carrega no botão de enviar
  async function handleSubmit(event) {
    event.preventDefault() // para a página não recarregar
    setSubmitting(true) // bloqueia o botão
    setFormError('') // limpa os erros do envio anterior
    setFieldErrors({})

    try {
      // chama a função que a página nos deu e espera
      await onSubmit(toPayload(values))
    } catch (error) {
      // se a página ou a api deram erro mostra aqui
      setFormError(error.message)
      setFieldErrors(error.fieldErrors ?? {})
    } finally {
      // corre sempre dê certo ou não e desbloqueia o botão
      setSubmitting(false)
    }
  }

  return (
    <form onSubmit={handleSubmit} noValidate>
      {/* erro geral tipo número repetido ou servidor desligado */}
      {formError && <p role="alert">{formError}</p>}

      {/* repete isto para cada campo da lista FIELDS */}
      {FIELDS.map(({ name, label, type }) => (
        <div key={name}>
          <label htmlFor={name}>{label}</label>
          <input
            id={name}
            name={name}
            type={type}
            value={values[name]}
            onChange={handleChange}
            // o número de estudante não pode mudar depois de criado
            readOnly={isEditing && name === 'studentNumber'}
          />
          {/* só aparece se o backend disse que este campo tem erro */}
          {fieldErrors[name] && <p id={`${name}-error`}>{fieldErrors[name]}</p>}
        </div>
      ))}

      {/* o estado só se escolhe a editar porque um estudante novo fica logo ativo */}
      {isEditing && (
        <div>
          <label htmlFor="status">Estado</label>
          <select id="status" name="status" value={values.status} onChange={handleChange}>
            {STUDENT_STATUSES.map(({ value, label }) => (
              <option key={value} value={value}>
                {label}
              </option>
            ))}
          </select>
          {fieldErrors.status && <p id="status-error">{fieldErrors.status}</p>}
        </div>
      )}

      {/* fica bloqueado a enviar para não mandar duas vezes */}
      <button type="submit" disabled={submitting}>
        {submitting ? 'A guardar...' : submitLabel}
      </button>
    </form>
  )
}

// para os outros ficheiros poderem importar o formulário
export default StudentForm