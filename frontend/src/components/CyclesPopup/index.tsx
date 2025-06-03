import * as React from 'react'
import { useEffect, useRef, useState } from 'react'
import { useParams } from 'react-router-dom'

import FullCalendar from '@fullcalendar/react'
import dayGridPlugin from '@fullcalendar/daygrid'
import interactionPlugin, { Draggable } from '@fullcalendar/interaction'

import { Popup } from '../Popup'
import { Button } from '../Button'

import { Mesocycle } from '../../types/Mesocycle'

import { parseToEpoch } from '../../../utils/parseToEpoch'
import { epochConverter } from '../../../utils/epochConverter'

import { createCalendar } from '../../../services/athleteServices'

import styles from './styles.module.css'
import { useAuthentication } from '../../hooks/useAuthentication'

type CyclesPopupProps = {
  onClose: () => void
  onSuccess: () => void
  cycles: Mesocycle[]
}

function convertCyclesToEvents(cycles: Mesocycle[]): any[] {
  const events: any[] = []

  for (const meso of cycles) {
    const hasLockedMicrocycle = meso.microcycles.some(micro => micro.activities.length > 0)

    events.push({
      id: `meso-${meso.id}`,
      title: 'Mesocycle',
      start: epochConverter(meso.startTime, 'yyyy-mm-dd'),
      end: epochConverter(meso.endTime, 'yyyy-mm-dd'),
      allDay: true,
      locked: hasLockedMicrocycle,
    })

    for (const micro of meso.microcycles) {
      const isLocked = micro.activities.length > 0
      events.push({
        id: `micro-${micro.id}`,
        title: 'Microcycle',
        start: epochConverter(micro.startTime, 'yyyy-mm-dd'),
        end: epochConverter(micro.endTime, 'yyyy-mm-dd'),
        allDay: true,
        locked: isLocked,
      })
    }
  }

  return events
}

export function CyclesPopup({ onClose, onSuccess, cycles }: CyclesPopupProps) {
  const [events, setEvents] = useState<any[]>(convertCyclesToEvents(cycles))
  const dragContainerRef = useRef<HTMLDivElement>(null)
  const id = useParams().aid
  const [user] = useAuthentication()

  function isValid(event: any, events: any[], currentEventId: string | null): boolean {
    if (event.extendedProps.locked) {
      alert('This event is locked and cannot be moved or resized.')
      return false
    }

    function isMesocycle(e: any) {
      return e.title === 'Mesocycle'
    }
    function isMicrocycle(e: any) {
      return e.title === 'Microcycle'
    }

    const isOverlapping = events.some(existingEvent => {
      if (existingEvent.id === currentEventId) return false
      if ((isMesocycle(event) && isMicrocycle(existingEvent)) || (isMicrocycle(event) && isMesocycle(existingEvent))) return false

      return event.startStr < existingEvent.end && event.endStr > existingEvent.start
    })

    if (isOverlapping) {
      alert(`${event.title}s cannot overlap with each other`)
      return false
    }

    if (isMicrocycle(event)) {
      const isMicrocycleInsideMesocycle = events.some(existingEvent => {
        if (!isMesocycle(existingEvent)) return false
        return event.startStr >= existingEvent.start && event.endStr <= existingEvent.end
      })

      if (!isMicrocycleInsideMesocycle) {
        alert('A microcycle can only be placed under a mesocycle')
        return false
      }
    }

    return true
  }

  function eventDraggableToEventOnCalendar(event: any) {
    return {
      id: event.id || Date.now().toString(),
      title: event.title,
      start: event.startStr,
      end: event.endStr,
      allDay: event.allDay,
      locked: event.extendedProps.locked || false,
    }
  }

  function handleReceive(info: any) {
    if (!isValid(info.event, events, null)) {
      info.revert()
      return
    }

    const event = eventDraggableToEventOnCalendar(info.event)

    setEvents(prev => [...prev, event])
  }

  function handleEvent(info: any) {
    if (!isValid(info.event, events, info.event.id)) {
      info.revert()
      return
    }

    const updatedEvent = eventDraggableToEventOnCalendar(info.event)

    setEvents(prev => prev.map(event => (event.id === updatedEvent.id ? updatedEvent : event)))
  }

  function eventRender(info: any) {
    const eventClass = info.event.title === 'Mesocycle' ? styles.mesocycle : styles.microcycle
    const lock = info.event.extendedProps.locked

    return (
      <span className={`${styles.event} ${eventClass}`}>
        <strong>
          {lock ? '🔒 ' : ''}
          {info.event.title}
        </strong>{' '}
        {info.event.start.toLocaleDateString()} - {info.event.end.toLocaleDateString()}
      </span>
    )
  }

  async function handleSubmit() {
    function associateMicrocyclesToMesocycles(events: any[]) {
      const mesocycles = events
        .filter(e => e.title === 'Mesocycle')
        .map(m => ({
          id: m.id?.startsWith('meso-') ? Number(m.id.split('-')[1]) : null,
          startTime: typeof m.start === 'number' ? m.start : new Date(m.start).getTime(),
          endTime: typeof m.end === 'number' ? m.end : new Date(m.end).getTime(),
          microcycles: [] as {
            id: number | null
            startTime: number
            endTime: number
          }[],
        }))

      const microcycles = events
        .filter(e => e.title === 'Microcycle')
        .map(mc => ({
          id: mc.id?.startsWith('micro-') ? Number(mc.id.split('-')[1]) : null,
          startTime: typeof mc.start === 'number' ? mc.start : new Date(mc.start).getTime(),
          endTime: typeof mc.end === 'number' ? mc.end : new Date(mc.end).getTime(),
        }))

      for (const micro of microcycles) {
        const parent = mesocycles.find(m => micro.startTime >= m.startTime && micro.endTime <= m.endTime)
        if (parent) parent.microcycles.push(micro)
      }

      return { mesocycles }
    }

    const payload = associateMicrocyclesToMesocycles(events)

    try {
      await createCalendar(id, payload)
      onSuccess()
    } catch (error) {
      alert('Error saving cycles')
    }
  }

  return (
    <Popup
      title={user.isCoach ? 'Manage Cycles' : ''}
      onClose={onClose}
      content={
        <div className={styles.container}>
          {user.isCoach && (
            <div className={styles.items} ref={dragContainerRef}>
              <h3>Items</h3>
              <Item title="Mesocycle" duration={30} />
              <Item title="Microcycle" duration={7} />
            </div>
          )}
          <div className={styles.calendar}>
            <FullCalendar
              height="70vh"
              plugins={[dayGridPlugin, interactionPlugin]}
              initialView="dayGridMonth"
              events={events}
              droppable={true}
              eventReceive={handleReceive}
              editable={true}
              eventDrop={handleEvent}
              eventResize={handleEvent}
              eventContent={eventRender}
              headerToolbar={{
                left: 'prevYear,prev,next,nextYear today',
                right: '',
                center: 'title',
              }}
            />
            {user.isCoach && <Button text="Save" onClick={handleSubmit} width="100px" height="40px" />}
          </div>
        </div>
      }
    />
  )
}

type ItemProps = {
  title: string
  duration: number
}

export function Item({ title, duration }: ItemProps) {
  const itemRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    if (itemRef.current) {
      new Draggable(itemRef.current, {
        itemSelector: '.fc-draggable-item',
        eventData: (eventElement: any) => {
          return {
            title: eventElement.innerText,
            duration: { days: duration },
            allDay: true,
          }
        },
      })
    }
  }, [])

  const itemClass = title === 'Mesocycle' ? styles.mesocycle : styles.microcycle

  return (
    <div ref={itemRef} className={`${styles.item} ${itemClass} fc-draggable-item`}>
      {title}
    </div>
  )
}
