import * as React from 'react';

import { Popup } from '../Popup';
import { Slider } from '@mui/material';
import { Button } from '../Button';

type AddQuestionnairePopupProps = {
    onClose: () => void;
};

export function AddQuestionnairePopup({ onClose }: AddQuestionnairePopupProps) {

    async function handleSubmit(event: React.FormEvent) {
        event.preventDefault();
        // Handle form submission logic here
    }

    return (
        <Popup
            title="Add Questionnaire"
            content={
                <form onSubmit={handleSubmit}>
                    <div>
                        <label>Sleep</label>
                        <Slider
                            name="Sleep"
                            value={undefined}
                            onChange={() => {}}
                            min={0}
                            max={10}
                            step={1}
                            marks
                            valueLabelDisplay="auto"
                        />
                    </div>
                    <div>
                        <label>Fatigue</label>
                        <Slider
                            name="fatigue"
                            value={undefined}
                            onChange={() => {}}
                            min={0}
                            max={10}
                            step={1}
                            marks
                            valueLabelDisplay="auto"
                        />
                    </div>
                    <div>
                        <label>Stress</label>
                        <Slider
                            name="pse"
                            value={undefined}
                            onChange={() => {}}
                            min={0}
                            max={10}
                            step={1}
                            marks
                            valueLabelDisplay="auto"
                        />
                    </div>
                    <div>
                        <label>Muscle Pain</label>
                        <Slider
                            name="pse"
                            value={undefined}
                            onChange={() => {}}
                            min={0}
                            max={10}
                            step={1}
                            marks
                            valueLabelDisplay="auto"
                        />
                    </div>
                    <Button text="Submit" type="submit" />
                </form>
            }
            onClose={onClose}
        />
    );
}