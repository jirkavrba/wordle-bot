import {Composition} from 'remotion';
import Wordle from './Wordle';

export const RemotionVideo: React.FC = () => {
	return (
		<>
			<Composition
				id="wordle"
				component={Wordle}
				durationInFrames={250} 
				fps={30}
				width={600}
				height={800}
				defaultProps={{
					index: 349,
					evaluation: "APAPPPAAACPAAACCCCCC"
				}}
			/>
		</>
	);
};
