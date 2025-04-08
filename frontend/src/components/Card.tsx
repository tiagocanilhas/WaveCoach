import * as React from 'react'

type CardProps = {
  content: React.ReactNode
}

const cardStyles: React.CSSProperties = {
  backgroundColor: '#fff',
  borderRadius: '10px',
  padding: '20px',
  boxShadow: '0 0 30px rgba(0, 0, 0, 0.3)',
  marginBottom: '20px',
  display: 'inline-block',
}

export function Card({ content }: CardProps) {
  return <div style={cardStyles}>{content}</div>
}
