import * as React from 'react'
import { ReactNode } from 'react'
import { Link } from 'react-router-dom'
import { useState } from 'react'
import { FaBars } from 'react-icons/fa'
import { AiOutlineClose } from 'react-icons/ai'
import { IconContext } from 'react-icons'
import { FaUser } from 'react-icons/fa'
import { RiLogoutBoxRFill } from 'react-icons/ri'

type SidebarItem = {
  title: string
  path: string
  icon?: ReactNode
  cName: string
}

type BarsProps = {
  title?: string
  sidebarData: SidebarItem[]
}

export function Bars({ title, sidebarData }: BarsProps) {
  const [showSidebar, setShowSidebar] = useState(false)

  function handleShowSidebar() {
    setShowSidebar(!showSidebar)
  }

  return (
    <IconContext.Provider value={{ color: '#fff' }}>
      <div className="bar-layout">
        <div className="topbar">
          <FaBars className="menu-bars" onClick={handleShowSidebar} />
          <h1 className="topbar-title">{title}</h1>
        </div>
        <div className={showSidebar ? 'sidebar active' : 'sidebar'}>
          <ul className="sidebar-items" onClick={handleShowSidebar}>
            <li className="sidebar-toggle">
              <AiOutlineClose className="menu-bars" />
            </li>
            {sidebarData.map((item, index) => {
              return (
                <li key={index} className={item.cName}>
                  <Link to={item.path}>
                    {item.icon}
                    <span>{item.title}</span>
                  </Link>
                </li>
              )
            })}
          </ul>
          <ul className="sidebar-bottom-items">
            <li className="sidebar-bottom-text">
              <Link to="/athletes">
                <span>Athletes</span>
              </Link>
            </li>
            <li className="sidebar-bottom-text">
              <Link to="/account">
                <FaUser />
                <span>Account</span>
              </Link>
            </li>
            <li className="sidebar-bottom-text">
              <Link to="/logout">
                <RiLogoutBoxRFill />
                <span>Logout</span>
              </Link>
            </li>
          </ul>
        </div>
      </div>
    </IconContext.Provider>
  )
}
