import { render, screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import App from './App'

describe('App', () => {
  it('apresenta o nome StudyMatch', () => {
    render(<App />)

    expect(
      screen.getByRole('heading', { name: /studymatch/i })
    ).toBeInTheDocument()
  })
})
