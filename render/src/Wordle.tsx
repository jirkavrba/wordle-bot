import { AbsoluteFill, Sequence } from "remotion";
import Evaluation from "./Wordle/Evaluation";
import { Title } from "./Wordle/Title";

interface WordleVideoProps {
  index: number,
  evaluation: string
}

const Wordle: React.FC<WordleVideoProps> = ({ index, evaluation }: WordleVideoProps) => {
  return (<>
    <AbsoluteFill style={{background: "white"}}>
      <Sequence from={0}> 
        <Title index={index} tries={evaluation.length / 5} />
      </Sequence>
      <Sequence from={50}>
        <Evaluation source={evaluation}/>
      </Sequence>
    </AbsoluteFill>
  </>)
}

export default Wordle;