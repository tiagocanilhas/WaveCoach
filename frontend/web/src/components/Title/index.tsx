import * as React from 'react'
import { ReactNode } from 'react'
import { Helmet } from 'react-helmet'

type TitleProps = {
  title: string
  content: ReactNode
}

export function Title({ title, content }: TitleProps) {
  const titlePrefix = 'Wave Coach'
  return (
    <>
      <Helmet>
        <title>{`${titlePrefix} - ${title}`}</title>
      </Helmet>
      {content}
    </>
  )
}
