import React, { useState } from 'react'

type PopupProps = {
  title?: string
  content: React.ReactNode
  onClose?: () => void
}

const overlayStyles: React.CSSProperties = {
  position: 'fixed',
  top: '0',
  left: '0',
  right: '0',
  bottom: '0',
  display: 'flex',
  justifyContent: 'center',
  alignItems: 'center',
  backgroundColor: 'rgba(0, 0, 0, 0.5)',
  zIndex: 1000,
  pointerEvents: 'auto',
}

const popupContainerStyles: React.CSSProperties = {
  position: 'relative',
  backgroundColor: '#fff',
  borderRadius: '8px',
  padding: '20px',
  maxWidth: '400px',
  width: '100%',
  boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)',
  pointerEvents: 'auto' as const,
}

const closeButtonStyles: React.CSSProperties = {
  position: 'absolute',
  top: '10px',
  left: '10px',
  background: 'none',
  border: 'none',
  fontSize: '1.5rem',
  color: '#aaa',
  cursor: 'pointer',
}

const titleStyles: React.CSSProperties = {
  fontSize: '1.25rem',
  fontWeight: 'bold',
  marginBottom: '10px',
}

const contentStyles: React.CSSProperties = {
  marginBottom: '20px',
}

const okButtonStyles: React.CSSProperties = {
  position: 'absolute',
  bottom: '10px',
  right: '10px',
  backgroundColor: '#3b82f6',
  color: '#fff',
  border: 'none',
  padding: '10px 20px',
  borderRadius: '4px',
  cursor: 'pointer',
}

export function Popup({ title, content, onClose }: PopupProps) {
  const [isOpen, setIsOpen] = useState(true)

  function handleClose() {
    setIsOpen(false)
    onClose && onClose()
  }

  function handleOK() {
    setIsOpen(false)
    onClose && onClose()
  }

  if (!isOpen) return null

  return (
    <div style={overlayStyles}>
      <div style={popupContainerStyles}>
        <button onClick={handleClose} style={closeButtonStyles}>
          X
        </button>

        {title && <h2 style={titleStyles}>{title}</h2>}

        <div style={contentStyles}>{content}</div>

        <button onClick={handleOK} style={okButtonStyles}>
          OK
        </button>
      </div>
    </div>
  )
}
