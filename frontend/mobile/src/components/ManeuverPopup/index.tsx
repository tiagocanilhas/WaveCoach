import * as React from 'react'
import { useState } from 'react'
import { View } from 'react-native'

import { Popup } from '@components/Popup'
import { Button } from '@components/Button'
import { Switch } from '@components/Switch'

import { Maneuver } from '@types/Maneuver'

import { styles } from './styles'

type ManeuverPopupProps = {
  title: string
  maneuver?: Maneuver
  onSave: (success: boolean) => void
  onClose: () => void
}

export function ManeuverPopup({ title, maneuver, onSave, onClose }: ManeuverPopupProps) {
  const [switchValue, setSwitchValue] = useState(maneuver?.success || false)

  function handleToggleSwitch() {
    setSwitchValue(s => !s)
  }

  function handleOnSave() {
    onSave(switchValue)
    onClose()
  }

  return (
    <Popup
      title={title}
      content={
        <View style={styles.container}>
          <Switch
            value={switchValue}
            onChange={handleToggleSwitch}
            leftLabel="Failure"
            rightLabel="Success"
            labelStyle={styles.label}
          />
          <Button text={maneuver ? 'Save' : 'Add'} onPress={handleOnSave} />
        </View>
      }
      onClose={onClose}
    />
  )
}
