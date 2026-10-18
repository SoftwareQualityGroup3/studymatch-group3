import { useState } from 'react'

// valores de início quando é um estudante novo
// as chaves são os mesmos nomes que o backend usa
const EMPTY_STUDENT = {
  number: '',
  name: '',
  email: '',
  course: '',
  curricularYear: '',
}

// lista dos campos do formulário, cada um vira uma label e uma caixa
// o min e o max só servem para o campo numérico
const FIELDS = [
  { name: 'number', label: 'Número de estudante', type: 'text' },
  { name: 'name', label: 'Nome', type: 'text' },
  { name: 'email', label: 'Contacto institucional (email)', type: 'email' },
  { name: 'course', label: 'Curso', type: 'text' },
  { name: 'curricularYear', label: 'Ano curricular', type: 'number', min: 1, max: 5 },
]

// passa os dados de um estudante para os valores das caixas
function toFormValues(student) {
  // sem estudante é criar, por isso começa tudo vazio
  if (!student) return EMPTY_STUDENT

  // com estudante é editar e o email e o curso podem vir a null
  // as caixas precisam de texto, por isso o null passa a vazio
  return {
    number: student.number ?? '',
    name: student.name ?? '',
    email: student.email ?? '',
    course: student.course ?? '',
    curricularYear: String(student.curricularYear ?? ''),
  }
}

// prepara os valores para mandar ao backend
function toPayload(values) {
  return {
    ...values,
    // as caixas guardam texto, por isso o ano passa a número
    // vazio vai a null e o backend responde com o erro desse campo
    curricularYear: values.curricularYear === '' ? null : Number(values.curricularYear),
  }
}

// o mesmo formulário serve para criar e editar
// recebe o estudante (só se for editar), o texto do botão e a função que corre ao enviar
// nota: os valores só são lidos na primeira vez, por isso quem usa isto
// tem de pôr uma key diferente por estudante (ver a página de edição)
function StudentForm({ initialStudent, submitLabel, onSubmit }) {
  // o que está escrito em cada caixa
  const [values, setValues] = useState(() => toFormValues(initialStudent))
  // fica true enquanto o pedido está a decorrer
  const [submitting, setSubmitting] = useState(false)
  // erro geral do formulário (ex: servidor desligado)
  const [formError, setFormError] = useState('')
  // erros de cada campo, tal como o backend os devolve
  const [fieldErrors, setFieldErrors] = useState({})

  // corre sempre que se escreve numa caixa
  function handleChange(event) {
    const { name, value } = event.target
    // copia os valores antigos e muda só o campo que foi escrito
    setValues((current) => ({ ...current, [name]: value }))
  }

  // corre quando se carrega no botão de enviar
  async function handleSubmit(event) {
    event.preventDefault() // para a página não recarregar
    setSubmitting(true) // bloqueia o botão
    setFormError('') // limpa os erros do envio anterior
    setFieldErrors({})

    try {
      // a página é que sabe se é criar ou editar, por isso é ela que envia
      await onSubmit(toPayload(values))
    } catch (error) {
      // erros da api (400, 409...) ou de rede, ficam todos no formulário
      setFormError(error.message)
      setFieldErrors(error.fieldErrors ?? {})
    } finally {
      // corre sempre, dê certo ou não, e desbloqueia o botão
      setSubmitting(false)
    }
  }

  return (
    // o novalidate deixa a validação para o backend, para vermos as mensagens dele
    <form onSubmit={handleSubmit} noValidate>
      {/* erro geral, tipo número repetido ou servidor desligado */}
      {formError && <p role="alert">{formError}</p>}

      {/* repete isto para cada campo da lista de campos lá de cima */}
      {FIELDS.map(({ name, label, type, min, max }) => (
        <div key={name}>
          <label htmlFor={name}>{label}</label>
          <input
            id={name}
            name={name}
            type={type}
            min={min}
            max={max}
            value={values[name]}
            onChange={handleChange}
            // liga a caixa à mensagem de erro para os leitores de ecrã
            aria-invalid={fieldErrors[name] ? 'true' : undefined}
            aria-describedby={fieldErrors[name] ? `${name}-error` : undefined}
          />
          {/* só aparece se o backend disse que este campo tem erro */}
          {fieldErrors[name] && <p id={`${name}-error`}>{fieldErrors[name]}</p>}
        </div>
      ))}

      {/* fica bloqueado a enviar para não mandar duas vezes */}
      <button type="submit" disabled={submitting}>
        {submitting ? 'A guardar...' : submitLabel}
      </button>
    </form>
  )
}

export default StudentForm
