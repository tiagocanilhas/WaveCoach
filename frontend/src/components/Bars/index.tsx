import * as React from 'react'
import { ReactNode, useState } from 'react'

import { TopBar } from '../../components/TopBar'
import { SideBar } from '../../components/SideBar'

type Item = {
  title: string
  path: string
  icon?: ReactNode
}

type BarsProps = {
    title: string
    sideBarItems: Item[]
}

export function Bars({ title, sideBarItems }: BarsProps) {
  const [isOpen, setIsOpen] = useState(false)

  function handleShowSidebar() {
    setIsOpen(!isOpen)
  }

  return (
    <>
      <TopBar title={title} isOpen={isOpen} handleShowSidebar={handleShowSidebar} />
      <SideBar isOpen={isOpen} sidebarData={sideBarItems} />
    </>
  )
}
